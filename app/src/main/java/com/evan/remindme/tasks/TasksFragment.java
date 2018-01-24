package com.evan.remindme.tasks;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.evan.remindme.R;
import com.evan.remindme.tasks.domain.model.Task;

import java.util.List;

import static com.evan.remindme.util.Objects.checkNotNull;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/24
 * Time: 下午8:08
 */
public class TasksFragment extends Fragment implements TasksContract.View {

    private TasksContract.Presenter mPresenter;

    private View mNoTaskView;

    private ImageView mNoTaskIcon;

    private TextView mNoTaskMainView;

    private TextView mNoTasksAddView;

    private LinearLayout mTasksView;

    private TextView mDisplayingLabelView;

    public TasksFragment(){
        //需要空的公共构造函数
    }

    public static TasksFragment newInstance(){
        return new TasksFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_display:
                showFilteringPopUpMenu();
                break;
        }
        return true;
    }

    @Override
    public void showNoTasks() {

    }

    @Override
    public void showNoSortTasks() {

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void showFilteringPopUpMenu() {
        PopupMenu popup = new PopupMenu(getContext(), getActivity().findViewById(R.id.menu_display));
        popup.getMenuInflater().inflate(R.menu.display_tasks, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.display_by_sort:
                        mPresenter.setDisplay();
                        break;
                    case R.id.display_by_time:
                        mPresenter.setDisplay();
                        break;
                }
                mPresenter.loadTasks();
                return false;
            }
        });
    }

    @Override
    public void setPresenter(TasksContract.Presenter presenter) {

    }

    private static class TasksAdapter extends BaseAdapter{

        private List<Task>mTasks;
        private TaskItemListener mItemListener;

        public TasksAdapter(List<Task>tasks, TaskItemListener itemListener){
            setList(tasks);
            mItemListener = itemListener;
        }

        public void replaceData(List<Task>tasks){
            setList(tasks);
            notifyDataSetChanged();
        }

        private void setList(List<Task>tasks){
            mTasks = checkNotNull(tasks);
        }

        @Override
        public int getCount() {
            return mTasks.size();
        }

        @Override
        public Task getItem(int i) {
            return mTasks.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return null;
        }
    }

    public interface TaskItemListener{

        void onTaskClick(Task clickTask);

        void onCompleteTaskClick(Task completedTask);

        void onActivateTaskClick(Task activatedTask);
    }
}
