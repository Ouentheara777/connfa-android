package com.ls.ui.fragment;

import com.ls.drupalcon.R;
import com.ls.drupalcon.model.Model;
import com.ls.drupalcon.model.UpdateRequest;
import com.ls.drupalcon.model.UpdatesManager;
import com.ls.drupalcon.model.data.InfoItem;
import com.ls.drupalcon.model.managers.InfoManager;
import com.ls.ui.activity.AboutDetailsActivity;
import com.ls.ui.view.TagBadgeSpannable;
import com.ls.utils.L;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 24.06.2016.
 */
public class AboutFragment extends Fragment {
    private ListView mListMenu;
    private View mLayoutPlaceholder;

    private AboutListAdapter adapter;
    private List<InfoItem> infoItems;

    private UpdatesManager.DataUpdatedListener updateListener = new UpdatesManager.DataUpdatedListener() {
        @Override
        public void onDataUpdated(List<UpdateRequest> requests) {
            L.d("AboutFragment");
            reloadData();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Model.instance().getUpdatesManager().registerUpdateListener(updateListener);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar();

        final String[] month = getResources().getStringArray(R.array.months);
//        Toolbar toolbar = (Toolbar) getView().findViewById(R.id.toolBar);
        android.support.v7.app.ActionBar toolbar = activity.getSupportActionBar();
        SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.months, android.R.layout.simple_spinner_dropdown_item);

        Spinner navigationSpinner = new Spinner(getContext());
        navigationSpinner.setAdapter(spinnerAdapter);
        if (toolbar != null) {
            toolbar.setCustomView(navigationSpinner);
            toolbar.setDisplayShowCustomEnabled(true);
            toolbar.setTitle("TEST");
            L.e("toolbar is not null");
        } else {
            L.e("toolbar is null");
        }

//        toolbar.addView(navigationSpinner, 0);

        navigationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "you selected: " + month[position], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.ac_about, container, false);
        initViews(result);
        return result;
    }

    @Override
    public void onDestroy() {
        Model.instance().getUpdatesManager().unregisterUpdateListener(updateListener);
        super.onDestroy();
    }

    private void initViews(View root) {

        mLayoutPlaceholder = root.findViewById(R.id.layout_placeholder);
        mListMenu = (ListView) root.findViewById(R.id.listView);
        mListMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                onItemClicked(position);
            }
        });

        if (adapter == null) {
            adapter = new AboutListAdapter(null, root.getContext());
            mListMenu.setAdapter(adapter);
        }

        reloadData();

    }

    private void reloadData() {
        InfoManager infoManager = Model.instance().getInfoManager();
        infoItems = infoManager.getInfo();

        if (infoItems == null || infoItems.isEmpty()) {
            mListMenu.setVisibility(View.GONE);
            mLayoutPlaceholder.setVisibility(View.VISIBLE);
        } else {
            mListMenu.setVisibility(View.VISIBLE);
            mLayoutPlaceholder.setVisibility(View.GONE);
            adapter.setData(infoItems);
            adapter.notifyDataSetChanged();
        }
    }

    private void onItemClicked(int position) {
        Activity root = getActivity();
        if (root == null) {
            return;
        }

        InfoItem item = infoItems.get(position);
        Intent intent = new Intent(root, AboutDetailsActivity.class);
        intent.putExtra(AboutDetailsActivity.EXTRA_DETAILS_TITLE, item.getTitle());
        intent.putExtra(AboutDetailsActivity.EXTRA_DETAILS_ID, item.getId());
        intent.putExtra(AboutDetailsActivity.EXTRA_DETAILS_CONTENT, item.getContent());
        startActivity(intent);
    }

    private class AboutListAdapter extends BaseAdapter {

        List<InfoItem> mItems = new ArrayList<>();
        LayoutInflater inflatter;

        public AboutListAdapter(List<InfoItem> items, Context context) {
            inflatter = LayoutInflater.from(context);
            setData(items);
        }

        public void setData(List<InfoItem> items) {
            if (items != null && !items.isEmpty()) {
                mItems = new ArrayList<>(items);
            } else {
                mItems = new ArrayList<>();
            }
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int i) {
            return mItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return mItems.get(i).getId();
        }

        @Override
        public View getView(int i, View view, ViewGroup parent) {
            View resultView;

            if (view == null) {
                resultView = inflatter.inflate(R.layout.item_about, parent, false);
            } else {
                resultView = view;
            }

            InfoItem item = mItems.get(i);
            ((TextView) resultView.findViewById(R.id.txtTitle)).setText(item.getTitle());

            return resultView;
        }
    }
}
