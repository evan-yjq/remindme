package com.evan.remindme.sorts;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.evan.remindme.R;
import com.evan.remindme.sorts.domain.model.Sort;
import com.evan.remindme.tasks.ScrollChildSwipeRefreshLayout;
import com.evan.remindme.util.Objects;

import java.util.ArrayList;
import java.util.List;

import static com.evan.remindme.util.Objects.checkNotNull;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/28
 * Time: 下午10:20
 */
public class SortsFragment extends Fragment implements SortsContract.View {

    private SortsContract.Presenter mPresenter;
    private LinearLayout mNoSortView;
    private ImageView mNoSortIcon;
    private TextView mNoSortMainView;
    private TextView mNoSortAddView;
    private LinearLayout mSortsView;
    private SortsAdapter mSortsAdapter;

    private ListView listView;

    public SortsFragment(){
    }

    public static SortsFragment newInstance(){
        return new SortsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSortsAdapter = new SortsAdapter(new ArrayList<Sort>(0),mItemListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.socks_frag,container,false);

        listView = root.findViewById(R.id.sorts_list);
        listView.setAdapter(mSortsAdapter);
        mSortsView = root.findViewById(R.id.sortsLL);

        mNoSortView = root.findViewById(R.id.noSorts);
        mNoSortAddView = root.findViewById(R.id.noSortsAdd);
        mNoSortIcon = root.findViewById(R.id.noSortsIcon);
        mNoSortMainView = root.findViewById(R.id.noSortsMain);
        mNoSortAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.addNewSort();
            }
        });

        FloatingActionButton fab = getActivity().findViewById(R.id.fab_add);
        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.addNewSort();
            }
        });
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadSorts(false);
            }
        });
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void setPresenter(SortsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setLoadingIndicator(final boolean active) {
        //显示加载动画

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
    public void showAddSort() {
        showDialog("新建分类",new DialogListener() {
            @Override
            public void onPositiveClick(EditText text) {
                String input = text.getText().toString();
                if (input.equals("")) {
                    showMessage("分类名不能为空");
                }else{
                    Sort sort = new Sort(input);
                    mPresenter.save(sort);
                }
            }
        },"");
    }

    private void showDialog(String title,DialogListener listener,String hint){
        TextInputLayout layout = (TextInputLayout) setPadding(new TextInputLayout(getActivity()));
        EditText et = new EditText(getActivity());
        et.setSingleLine();
        et.setHint(hint);
        layout.addView(et);

        MyDialogFragment dialog = new MyDialogFragment(title,layout,listener);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        dialog.show(ft,"Dialog");
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showSorts(List<Sort>sorts) {
        mSortsAdapter.replaceData(sorts);

        mSortsView.setVisibility(View.VISIBLE);
        mNoSortView.setVisibility(View.GONE);
    }

    @Override
    public void showNoSort() {
        mSortsView.setVisibility(View.GONE);
        mNoSortView.setVisibility(View.VISIBLE);

        mNoSortMainView.setText(R.string.no_sorts_all);
        mNoSortIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_assignment_turned_in_24dp));
        mNoSortAddView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showMessage(String message){
        Snackbar.make(getView(),message,Snackbar.LENGTH_LONG).show();
    }


    @Override
    public void showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_sort_message));
    }

    @Override
    public void showSortDeleteError() {
        showMessage(getString(R.string.delete_sort_error));
    }

    @Override
    public void showSortSaveError() {
        showMessage(getString(R.string.save_sort_error));
    }

    @Override
    public void showSortRename(final Sort sort) {
        showDialog("修改分类名", new DialogListener() {
            @Override
            public void onPositiveClick(EditText text) {
                String input = text.getText().toString();
                if (input.equals("")) {
                    showMessage("分类名不能为空");
                }else if(!Objects.equal(input,sort.getName())){
                    sort.setName(input);
                    mPresenter.rename(sort);
                }
            }
        },sort.getName());
    }

    @Override
    public void showSortRenameError() {
        showMessage(getString(R.string.rename_sort_error));
    }

    @Override
    public void showLoadingSortsError() {
        showMessage(getString(R.string.loading_sorts_error));
    }

    ListItemListener mItemListener = new ListItemListener() {
        @Override
        public void onSortLongClick(Sort sort) {
            // TODO 删除界面
        }

        @Override
        public void onSortClick(Sort sort) {
            showSortRename(sort);
        }
    };

    private static class SortsAdapter extends BaseAdapter{

        private List<Sort>mSorts;
        private ListItemListener mItemListener;

        public SortsAdapter(List<Sort>sorts,ListItemListener itemListener){
            mItemListener = itemListener;
            setList(sorts);
        }

        public void replaceData(List<Sort>sorts){
            setList(sorts);
            notifyDataSetChanged();
        }

        private void setList(List<Sort>sorts){
            mSorts = checkNotNull(sorts);
        }

        @Override
        public int getCount() {
            return mSorts.size();
        }

        @Override
        public Sort getItem(int i) {
            return mSorts.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            if (view == null){
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                view = inflater.inflate(R.layout.sort_item,viewGroup,false);
            }

            TextView titleTV = view.findViewById(R.id.title);
            titleTV.setText(getItem(i).getName());

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mItemListener.onSortLongClick(getItem(i));
                    return true;
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemListener.onSortClick(getItem(i));
                }
            });
            return view;
        }
    }

    @NonNull
    private View setPadding(View v){
        v.setPaddingRelative((int)(getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin)*1.3),
                getResources().getDimensionPixelOffset(R.dimen.activity_vertical_margin),
                (int)(getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin)*1.3),
                getResources().getDimensionPixelOffset(R.dimen.activity_vertical_margin));
        return v;
    }

    public static class MyDialogFragment extends DialogFragment {
        private String mTitle;
        private DialogListener mListener;
        private TextInputLayout mLayout;
        public MyDialogFragment(String title,TextInputLayout layout, DialogListener listener){
            mTitle = title;
            mListener = listener;
            mLayout = layout;
        }
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(mTitle)
                    .setView(mLayout)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mListener.onPositiveClick((EditText) mLayout.getChildAt(0));
                        }
                    })
                    .setNegativeButton("取消",null);
            return builder.create();
        }
    }

    public interface DialogListener{
        void onPositiveClick(EditText text);
    }

    public interface ListItemListener{
        void onSortLongClick(Sort sort);
        void onSortClick(Sort sort);
    }
}
