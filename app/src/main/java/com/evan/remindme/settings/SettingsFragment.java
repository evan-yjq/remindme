package com.evan.remindme.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.evan.remindme.R;
import com.evan.remindme.settings.domain.model.Setting;
import com.evan.remindme.tasks.ScrollChildSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/28
 * Time: 下午5:18
 */
public class SettingsFragment extends Fragment implements SettingsContract.View{

    private SettingsContract.Presenter mPresenter;
    private SettingsAdapter mAdapter;

    public SettingsFragment(){}

    public static SettingsFragment newInstance(){
        return new SettingsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new SettingsAdapter(new ArrayList<Setting>(0),mItemListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.settings_frag,container,false);

        ListView listView = root.findViewById(R.id.settings_list);
        listView.setAdapter(mAdapter);

        final ScrollChildSwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadSettings(false);
            }
        });
        swipeRefreshLayout.setScrollUpChild(listView);
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void setPresenter(SettingsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setLoadingIndicator(final boolean active) {
        if (getView() == null)return;
        final SwipeRefreshLayout srl = getView().findViewById(R.id.refresh_layout);
        //确保setRefreshing（）在布局完成后再调用。
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showSettings(List<Setting> settings) {
        mAdapter.replaceData(settings);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(getView(),message,Snackbar.LENGTH_LONG).show();
    }

    ListItemListener mItemListener = new ListItemListener() {

        @Override
        public void onSwitchOpen(Setting setting) {
            setting.setValue("true");
            mPresenter.edit(setting);
        }

        @Override
        public void onSwitchClose(Setting setting) {
            setting.setValue("false");
            mPresenter.edit(setting);
        }
    };

    //todo
    private static class SettingsAdapter extends BaseAdapter{

        private List<Setting>mSettings;
        private ListItemListener mItemListener;

        public SettingsAdapter(List<Setting>settings,ListItemListener listItemListener){
            mItemListener = listItemListener;
            setList(settings);
        }

        public void replaceData(List<Setting>settings){
            setList(settings);
            notifyDataSetChanged();
        }

        private void setList(List<Setting>settings){
            mSettings = settings;
        }

        @Override
        public int getCount() {
            return mSettings.size();
        }

        @Override
        public Setting getItem(int i) {
            return mSettings.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final Setting setting = getItem(i);
            if (view == null){
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                if (Objects.equals(setting.getDisplay(),SettingDisplay.SWITCH_ITEM.toString())){
                    view = inflater.inflate(R.layout.settings_switch_item,viewGroup,false);
                }
            }

            TextView titleTV = view.findViewById(R.id.title);
            titleTV.setText(setting.getTitle());
            if (Objects.equals(setting.getDisplay(),SettingDisplay.SWITCH_ITEM.toString())) {
                Switch switch_ = view.findViewById(R.id.switch_);
                switch_.setChecked(Boolean.parseBoolean(setting.getValue()));

                switch_.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b){
                            mItemListener.onSwitchOpen(setting);
                        }else{
                            mItemListener.onSwitchClose(setting);
                        }
                    }
                });
            }
            return view;
        }
    }

    interface ListItemListener{

        void onSwitchOpen(Setting setting);

        void onSwitchClose(Setting setting);
    }
}
