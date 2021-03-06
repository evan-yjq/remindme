package com.evan.remindme.allclassify;


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
import android.view.*;
import android.widget.*;
import com.evan.remindme.R;
import com.evan.remindme.allclassify.domain.model.Classify;
import com.evan.remindme.tasks.ScrollChildSwipeRefreshLayout;
import com.evan.remindme.util.Objects;

import java.util.ArrayList;
import java.util.List;

import static com.evan.remindme.tasks.TasksFragment.setEmptyView;
import static com.evan.remindme.util.Objects.checkNotNull;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/28
 * Time: 下午10:20
 */
public class AllClassifyFragment extends Fragment implements AllClassifyContract.View {

    private AllClassifyContract.Presenter mPresenter;
    private LinearLayout mNoClassifyView;
    private ImageView mNoClassifyIcon;
    private TextView mNoClassifyMainView;
    private TextView mNoClassifyAddView;
    private LinearLayout mAllClassifyView;
    private AllClassifyAdapter mAllClassifyAdapter;
    private ListView mListView;

    public AllClassifyFragment(){
    }

    public static AllClassifyFragment newInstance(){
        return new AllClassifyFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAllClassifyAdapter = new AllClassifyAdapter(new ArrayList<Classify>(0), mItemListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_add);
        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.addNewClassify();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.classify_frag,container,false);

        View view = setEmptyView(getActivity(),inflater.inflate(R.layout.task_item,null),R.dimen.classify_empty);

        mListView = root.findViewById(R.id.allClassify_list);
        mListView.addFooterView(view,null,false);
        mListView.setAdapter(mAllClassifyAdapter);
        mAllClassifyView = root.findViewById(R.id.allClassifyLL);

        mNoClassifyView = root.findViewById(R.id.noClassify);
        mNoClassifyAddView = root.findViewById(R.id.noClassifyAdd);
        mNoClassifyIcon = root.findViewById(R.id.noClassifyIcon);
        mNoClassifyMainView = root.findViewById(R.id.noClassifyMain);
        mNoClassifyAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.addNewClassify();
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
                mPresenter.loadAllClassify(false);
            }
        });
        swipeRefreshLayout.setScrollUpChild(mListView);
//        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void setPresenter(AllClassifyContract.Presenter presenter) {
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
    public void showAddClassify() {
        showDialog("新建分类",new DialogListener() {
            @Override
            public void onPositiveClick(EditText text) {
                String input = text.getText().toString();
                if (input.equals("")) {
                    showMessage("分类名不能为空");
                }else{
                    mPresenter.save(input);
                }
            }
        }, "",true);
    }

    private void showDialog(String title, DialogListener listener, String hint,boolean isShowText){
        TextInputLayout layout = null;
        if (isShowText) {
            layout = (TextInputLayout) setPadding(new TextInputLayout(getActivity()));
            EditText et = new EditText(getActivity());
            et.setSingleLine();
            if (!hint.isEmpty()) {
                et.setHint(hint);
                et.setText(hint);
            }
            layout.addView(et);
        }
        MyDialogFragment dialog = new MyDialogFragment(title,layout,listener, null);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        dialog.show(ft,"Dialog");
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showAllClassify(List<Classify> classifies) {
        mAllClassifyAdapter.replaceData(classifies);

        mAllClassifyView.setVisibility(View.VISIBLE);
        mNoClassifyView.setVisibility(View.GONE);
    }

    private void showDeleteDialog(final Classify classify){
        showDialog("是否删除该分类", new DialogListener() {
            @Override
            public void onPositiveClick(EditText text) {
                mPresenter.delete(classify);
            }
        },"",false);
    }

    @Override
    public void showNoClassify() {
        mAllClassifyView.setVisibility(View.GONE);
        mNoClassifyView.setVisibility(View.VISIBLE);

        mNoClassifyMainView.setText(R.string.no_classify_all);
        mNoClassifyIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_assignment_turned_in_24dp));
        mNoClassifyAddView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showMessage(String message){
        Snackbar.make(getView(),message,Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_classify_message));
    }

    @Override
    public void showClassifyDeleteError() {
        showMessage(getString(R.string.delete_classify_error));
    }

    @Override
    public void showClassifySaveError() {
        showMessage(getString(R.string.save_classify_error));
    }

    @Override
    public void showClassifyRename(final Classify classify) {
        showDialog("修改分类名", new DialogListener() {
            @Override
            public void onPositiveClick(EditText text) {
                String input = text.getText().toString();
                if (input.equals("")) {
                    showMessage("分类名不能为空");
                }else if(!Objects.equal(input, classify.getName())){
                    mPresenter.rename(classify,input);
                }
            }
        }, classify.getName(),true);
    }

    @Override
    public void showClassifyRenameError() {
        showMessage(getString(R.string.rename_classify_error));
    }

    @Override
    public void showLoadingAllClassifyError() {
        showMessage(getString(R.string.loading_all_classify_error));
    }

    ListItemListener mItemListener = new ListItemListener() {
        @Override
        public void onClassifyLongClick(Classify classify) {
            showDeleteDialog(classify);
        }

        @Override
        public void onClassifyClick(Classify classify) {
            if (Objects.equal(classify.getId(),(long)1)){
                showAddClassify();
            }else {
                showClassifyRename(classify);
            }
        }
    };

    public static class AllClassifyAdapter extends BaseAdapter{

        private List<Classify> mClassifies;
        private ListItemListener mItemListener;

        public AllClassifyAdapter(List<Classify> classifies, ListItemListener itemListener){
            mItemListener = itemListener;
            setList(classifies);
        }

        public void replaceData(List<Classify> classifies){
            setList(classifies);
            notifyDataSetChanged();
        }

        private void setList(List<Classify> classifies){
            mClassifies = checkNotNull(classifies);
        }

        @Override
        public int getCount() {
            return mClassifies.size();
        }

        @Override
        public Classify getItem(int i) {
            return mClassifies.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            if (view == null){
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                view = inflater.inflate(R.layout.classify_item,viewGroup,false);
            }

            final Classify classify = getItem(i);

            TextView titleTV = view.findViewById(R.id.title);
            titleTV.setText(classify.getName());

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mItemListener.onClassifyLongClick(classify);
                    return true;
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemListener.onClassifyClick(classify);
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
        private DialogInterface.OnClickListener cancelListener;
        public MyDialogFragment(String title,TextInputLayout layout, DialogListener listener,DialogInterface.OnClickListener cancel){
            mTitle = title;
            mListener = listener;
            mLayout = layout;
            cancelListener = cancel;
        }
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(mTitle)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (mLayout!=null)mListener.onPositiveClick(mLayout.getEditText());
                            else mListener.onPositiveClick(null);
                        }
                    })
                    .setNegativeButton("取消",cancelListener);
            if (mLayout!=null)builder.setView(mLayout);
            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            return dialog;
        }
    }

    public interface DialogListener{
        void onPositiveClick(EditText text);
    }

    public interface ListItemListener{
        void onClassifyLongClick(Classify classify);
        void onClassifyClick(Classify classify);
    }
}
