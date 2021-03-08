package com.dmdmax.goonj.screens.dialogs

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.dmdmax.goonj.R
import com.dmdmax.goonj.models.City
import com.dmdmax.goonj.network.client.NetworkOperationListener
import com.dmdmax.goonj.network.client.RestClient
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.Logger
import org.json.JSONArray


class DialogManager {

    fun getNoNetworkDialog(context: Context): AlertDialog.Builder? {
        val builder = AlertDialog.Builder(context, R.style.MyAlertDialogTheme)
        builder.setTitle("Alert!")
        builder.setMessage("Weak or no internet connection. Please try again later")
        builder.setCancelable(false)
        return builder
    }

    interface LocationPermissionClickListener{
        fun onPositiveButtonClick();
        fun onNegativeButtonClick();
        fun onDontAskClick();
    }

    interface CitySelectionListener{
        fun onCitySelected(city: City);
    }

    fun grantLocationPermission(context: Context, listener: LocationPermissionClickListener?) {
        val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_message, null);
        view.findViewById<TextView>(R.id.title).text = "Location Permission"
        view.findViewById<TextView>(R.id.message).text = "${context.resources.getString(R.string.app_name)} wants location permission in order to show you the Namaz time. Would you like to give that permission?"

        val builder = AlertDialog.Builder(context, R.style.MyAlertDialogTheme)
        builder.setCancelable(false)
        builder.setView(view);
        builder.setNegativeButton("No", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog!!.dismiss()
                listener!!.onNegativeButtonClick();
            }
        });
        builder.setPositiveButton("Yes, sure", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog!!.dismiss()
                listener!!.onPositiveButtonClick();
            }
        });

        builder.setNeutralButton("Don't Ask", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog!!.dismiss()
                listener!!.onDontAskClick();
            }
        });
        builder.show();
    }

    fun displayLocationOffDialog(context: Context, listener: LocationPermissionClickListener?) {
        val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_message, null);
        view.findViewById<TextView>(R.id.title).text = "GPS Disabled"
        view.findViewById<TextView>(R.id.message).text = "GPS on your device is disabled, please click Switch On GPS button in order to switch it on."

        val builder = AlertDialog.Builder(context, R.style.MyAlertDialogTheme)
        builder.setCancelable(false);
        builder.setView(view);

        builder.setPositiveButton("Switch On GPS", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog!!.dismiss()
                listener!!.onPositiveButtonClick();
            }
        });

        builder.show();
    }

    fun displayCityDialog(context: Context, listener: CitySelectionListener?) {
        val view: View = LayoutInflater.from(context).inflate(R.layout.city_list_dialog, null);
        val mList: ListView = view.findViewById(R.id.cities);
        val mEditText: EditText = view.findViewById(R.id.search_bar_edittext);

        val mDialog: AlertDialog;
        val builder = AlertDialog.Builder(context, R.style.MyAlertDialogTheme)
        builder.setCancelable(false);
        builder.setView(view);
        builder.setNegativeButton("Cancel", object: DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog?.cancel();
            }
        })
        mDialog = builder.create();

        RestClient(context, Constants.API_BASE_URL + Constants.Companion.EndPoints.CITIES, RestClient.Companion.Method.GET, null, object : NetworkOperationListener {
            override fun onSuccess(response: String?) {

                val cities: ArrayList<City> = arrayListOf();

                try {
                    val rootArray = JSONArray(response);
                    for (i in 0 until rootArray.length()) {
                        cities.add(City(
                                rootArray.getJSONObject(i).getString("city"),
                                rootArray.getJSONObject(i).getString("lat"),
                                rootArray.getJSONObject(i).getString("lng"),
                                rootArray.getJSONObject(i).getString("province")
                        ));
                    }

                    mDialog.findViewById<ProgressBar>(R.id.progress_bar).visibility = View.GONE;
                    mDialog.findViewById<ListView>(R.id.cities).visibility = View.VISIBLE;

                    mList.adapter = CityAdapter(context, cities);
                    mList.setOnItemClickListener(object : AdapterView.OnItemClickListener {
                        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            mDialog?.cancel();
                            listener?.onCitySelected(mList.adapter.getItem(position) as City);
                        }
                    });

                    mEditText.addTextChangedListener(object : TextWatcher {
                        override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                            (mList.adapter as CityAdapter).getFilter()!!.filter(arg0)
                        }

                        override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                        }

                        override fun afterTextChanged(arg0: Editable) {}
                    })

                } catch (e: Exception) {
                    e.printStackTrace();
                }
            }

            override fun onFailed(code: Int, reason: String?) {
                TODO("Not yet implemented")
            }
        }).exec();

        mDialog.show();
    }

    private inner class CityAdapter: BaseAdapter, Filterable {
        val context: Context;
        var list: ArrayList<City>;
        val mFilterList: ArrayList<City>;
        private var valueFilter: ValueFilter? = null


        constructor(context: Context, list: ArrayList<City>){
            this.context = context;
            this.list = list;
            this.mFilterList = list;
            getFilter();
        }

        override fun getCount(): Int {
            return list.size;
        }

        override fun getItem(position: Int): Any {
            return list.get(position);
        }

        override fun getItemId(position: Int): Long {
            return position.toLong();
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var view: View = LayoutInflater.from(context).inflate(R.layout.city, null, false);
            view.findViewById<TextView>(R.id.city_name).text = list.get(position).getCity();
            return view;
        }

        override fun getFilter(): Filter? {
            if (valueFilter == null) {
                valueFilter = ValueFilter()
            }
            return valueFilter
        }

        private inner class ValueFilter : Filter() {
            //Invoked in a worker thread to filter the data according to the constraint.
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                if (constraint != null && constraint.length > 0) {
                    val filterList: ArrayList<City> = arrayListOf();

                    for (i in 0 until mFilterList.size) {
                        if (mFilterList.get(i).getCity().toUpperCase().contains(constraint.toString().toUpperCase())) {
                            filterList.add(mFilterList.get(i));
                        }
                    }
                    results.count = filterList.size
                    results.values = filterList
                } else {
                    results.count = mFilterList.size
                    results.values = mFilterList
                }
                return results
            }

            //Invoked in the UI thread to publish the filtering results in the user interface.
            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                list = results.values as ArrayList<City>;
                notifyDataSetChanged()
            }
        }
    }
}