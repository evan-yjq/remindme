package com.evan.remindme.taskdetail;

import android.app.Dialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.evan.remindme.BasePresenter;
import com.evan.remindme.R;
import com.evan.remindme.addedittask.AddEditTaskActivity;
import com.evan.remindme.addedittask.AddEditTaskFragment;
import com.evan.remindme.allclassify.AllClassifyFragment;
import com.evan.remindme.allclassify.domain.model.Classify;
import com.evan.remindme.tasks.ScrollChildSwipeRefreshLayout;

import java.util.*;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/2/4
 * Time: 下午4:27
 */
public class TaskDetailFragment extends Fragment implements TaskDetailContract.View {

    private TaskDetailContract.Presenter mPresenter;

    private ListView mDetailList;

    private SimpleAdapter mListAdapter;

    @NonNull
    private static final String ARGUMENT_TASK_ID = "TASK_ID";

    @NonNull
    private static final int REQUEST_EDIT_TASK = 1;

    public TaskDetailFragment(){}

    public static TaskDetailFragment newInstance(String taskId){
        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_TASK_ID, taskId);
        TaskDetailFragment fragment = new TaskDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Set up floating action button
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_edit_task);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.editTask();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.taskdetail_frag, container, false);
        setHasOptionsMenu(true);
        mDetailList = root.findViewById(R.id.task_detail_list);
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadTask();
            }
        });
        return root;
    }

    @Override
    public void setLoadingIndicator(final boolean active) {
        //显示加载动画
        if (getView() == null)return;
        final ScrollChildSwipeRefreshLayout srl = getView().findViewById(R.id.refresh_layout);
        //确保setRefreshing（）在布局完成后再调用。
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit:
                mPresenter.editTask();
                break;
            case R.id.menu_move:
                mPresenter.getClassify(false);
                break;
            case R.id.menu_copy:
                mPresenter.getClassify(true);
                break;
            case R.id.menu_delete:
                mPresenter.deleteTask();
                return true;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.taskdetail_fragment_menu, menu);
    }
    @Override
    public void showEditTask(@NonNull String taskId) {
        Intent intent = new Intent(getContext(), AddEditTaskActivity.class);
        intent.putExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID, taskId);
        startActivityForResult(intent, REQUEST_EDIT_TASK);
    }

    @Override
    public void showMissingTask(List<Map<String,String>>errors) {
        mListAdapter = new SimpleAdapter(getActivity(),errors,R.layout.task_detail_text_item,
                new String[]{"title","value"},new int[]{R.id.detail_title,R.id.detail_value});
        mDetailList.setAdapter(mListAdapter);
        showMessage(getString(R.string.no_data));
    }

    @Override
    public void showSelectClassifyDialog(List<Classify>classifies, final boolean isCopy) {
        String title;
        if (isCopy) title = "复制到：";
        else title = "移动到：";
        MyDialogFragment dialog = new MyDialogFragment(getActivity(),title,classifies,isCopy,mPresenter);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        dialog.show(ft,"Dialog");
    }

    @Override
    public void showLoadAllClassifyError() {
        showMessage(getString(R.string.loading_all_classify_error));
    }

    @Override
    public void showDeleteError() {
        showMessage(getString(R.string.delete_error));
    }

    @Override
    public void showCopyOk() {
        showMessage(getString(R.string.copy_ok));
    }

    @Override
    public void showCopyError() {
        showMessage(getString(R.string.copy_error));
    }

    @Override
    public void showMoveOk() {
        showMessage(getString(R.string.move_ok));
    }

    @Override
    public void showMoveError() {
        showMessage(getString(R.string.move_error));
    }

    private void showMessage(String message){
        Snackbar.make(Objects.requireNonNull(getView()),message,Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showDetails(List<Map<String,String>> details) {
        mListAdapter = new SimpleAdapter(getActivity(),details,R.layout.task_detail_text_item,
                new String[]{"title","value"},new int[]{R.id.detail_title,R.id.detail_value});
        mDetailList.setAdapter(mListAdapter);
    }

    @Override
    public void showTaskDeleted() {
        getActivity().finish();
    }

    @Override
    public void setPresenter(TaskDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    private static class MyDialogFragment extends DialogFragment implements AllClassifyFragment.ListItemListener{
        private String mTitle;
        private ListView mLayout;
        private boolean isCopy;
        private TaskDetailContract.Presenter mPresenter;
        public MyDialogFragment(Context context, String title, List<Classify>classifies, boolean isCopy, TaskDetailContract.Presenter presenter){
            mTitle = title;
            mPresenter = presenter;
            this.isCopy = isCopy;
            mLayout = new ListView(context);
            AllClassifyFragment.AllClassifyAdapter adapter = new AllClassifyFragment.AllClassifyAdapter(classifies,this);
            mLayout.setAdapter(adapter);
        }
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(mTitle)
                    .setView(mLayout);
            Dialog dialog = builder.create();
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            return dialog;
        }

        @Override
        public void onClassifyLongClick(Classify classify) {}

        @Override
        public void onClassifyClick(Classify classify) {
            if (isCopy)mPresenter.copyTask(classify);
            else mPresenter.moveTask(classify);
            this.dismiss();
        }
    }
}
