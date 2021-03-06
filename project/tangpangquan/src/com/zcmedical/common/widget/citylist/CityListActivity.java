package com.zcmedical.common.widget.citylist;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.tangpangquan.R;

public class CityListActivity extends BaseActivity implements TextWatcher {
    private Context context_ = CityListActivity.this;

    private ContactListViewImpl listview;

    private EditText searchBox;
    private String searchString;

    private Object searchLock = new Object();
    boolean inSearchMode = false;

    private final static String TAG = "cth";

    List<ContactItemInterface> contactList;
    List<ContactItemInterface> filterList;
    private SearchListTask curSearchTask = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.citylist);

        initToolbar("城市选择");

        filterList = new ArrayList<ContactItemInterface>();
        contactList = CityData.getSampleContactList();

        CityAdapter adapter = new CityAdapter(this, R.layout.city_item, contactList);

        listview = (ContactListViewImpl) this.findViewById(R.id.listview);
        listview.setFastScrollEnabled(true);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                List<ContactItemInterface> searchList = inSearchMode ? filterList : contactList;

                String city = searchList.get(position).getDisplayInfo();
                if (!TextUtils.isEmpty(city)) {
                    Intent cityData = new Intent();
                    cityData.putExtra(APIKey.USER_CITY, city);
                    setResult(RESULT_OK, cityData);
                    
                    finish();
                }
                //                Toast.makeText(context_, searchList.get(position).getDisplayInfo(), Toast.LENGTH_SHORT).show();
            }
        });

        searchBox = (EditText) findViewById(R.id.input_search_query);
        searchBox.addTextChangedListener(this);
    }

    @Override
    public void afterTextChanged(Editable s) {
        searchString = searchBox.getText().toString().trim().toUpperCase();

        if (curSearchTask != null && curSearchTask.getStatus() != AsyncTask.Status.FINISHED) {
            try {
                curSearchTask.cancel(true);
            } catch (Exception e) {
                Log.i(TAG, "Fail to cancel running search task");
            }

        }
        curSearchTask = new SearchListTask();
        curSearchTask.execute(searchString);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // do nothing
    }

    private class SearchListTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            filterList.clear();

            String keyword = params[0];

            inSearchMode = (keyword.length() > 0);

            if (inSearchMode) {
                // get all the items matching this
                for (ContactItemInterface item : contactList) {
                    CityItem contact = (CityItem) item;

                    boolean isPinyin = contact.getFullName().toUpperCase().indexOf(keyword) > -1;
                    boolean isChinese = contact.getNickName().indexOf(keyword) > -1;

                    if (isPinyin || isChinese) {
                        filterList.add(item);
                    }

                }

            }
            return null;
        }

        protected void onPostExecute(String result) {

            synchronized (searchLock) {

                if (inSearchMode) {

                    CityAdapter adapter = new CityAdapter(context_, R.layout.city_item, filterList);
                    adapter.setInSearchMode(true);
                    listview.setInSearchMode(true);
                    listview.setAdapter(adapter);
                } else {
                    CityAdapter adapter = new CityAdapter(context_, R.layout.city_item, contactList);
                    adapter.setInSearchMode(false);
                    listview.setInSearchMode(false);
                    listview.setAdapter(adapter);
                }
            }

        }
    }

}
