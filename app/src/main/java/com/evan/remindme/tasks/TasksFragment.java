package com.evan.remindme.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.*;
import android.widget.*;
import com.evan.remindme.R;
import com.evan.remindme.tasks.domain.model.Task;
import com.evan.remindme.util.Objects;

import java.util.ArrayList;
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

    private TasksAdapter mListAdapter;

    private View mNoTaskView;

    private ImageView mNoTaskIcon;

    private TextView mNoTaskMainView;

    private TextView mNoTaskAddView;

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
        mListAdapter = new TasksAdapter(new ArrayList<Task>(0),mItemListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode,resultCode);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.tasks_frag,container,false);

        //设置提醒界面
        ListView listView = root.findViewById(R.id.tasks_list);
        listView.setAdapter(mListAdapter);
        mDisplayingLabelView = root.findViewById(R.id.displayingLabel);
        mTasksView = root.findViewById(R.id.tasksLL);

        //设置没有提醒时的界面
        mNoTaskView = root.findViewById(R.id.noTasks);
        mNoTaskIcon = root.findViewById(R.id.noTasksIcon);
        mNoTaskMainView = root.findViewById(R.id.noTasksMain);
        mNoTaskAddView = root.findViewById(R.id.noTasksAdd);
        mNoTaskAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddTask();
            }
        });

        //设置浮动按钮
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_add_task);

        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.addNewTask();
            }
        });

        //设置进度指示器
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.refresh_layout);

        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        //在自定义的SwipeRefreshLayout中设置滚动视图
        swipeRefreshLayout.setScrollUpChild(listView);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadTasks(false);
            }
        });

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_display:
                showDisplayingPopUpMenu();
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tasks_fragment_menu,menu);
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
    public void showTasks(List<Task> tasks) {
        mListAdapter.replaceData(tasks);

        mTasksView.setVisibility(View.VISIBLE);
        mNoTaskView.setVisibility(View.GONE);
    }

    @Override
    public void showAddTask() {
        // TODO 等有了添加页面来解除注释
//        Intent intent = new Intent(getContext(), AddEditTaskActivity.class);
//        startActivityForResult(intent, AddEditTaskActivity.REQUEST_ADD_TASK);
    }

    @Override
    public void showTaskDetailsUi(Long taskId) {
        // TODO 等有了详情页面来解除注释
        //在它自己的Activity中，因为这样做更有意义，它使我们可以灵活地显示一些Intent存根。
//        Intent intent = new Intent(getContext(), TaskDetailActivity.class);
//        intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, taskId);
//        startActivity(intent);
    }

    @Override
    public void showNoTasks() {
        showNoTasksViews(
                getResources().getString(R.string.no_tasks_all),
                R.drawable.ic_assignment_turned_in_24dp,
                false
        );
    }

    @Override
    public void showNoSortTasks() {
        showNoTasksViews(
                getResources().getString(R.string.no_tasks_all),
                R.drawable.ic_assignment_turned_in_24dp,
                false
        );
    }

    private void showMessage(String message){
        Snackbar.make(getView(),message,Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showTaskMarkedTurnOn() {
        showMessage(getString(R.string.task_marked_turn_on));
    }

    @Override
    public void showTaskMarkedTurnOff() {
        showMessage(getString(R.string.task_marked_turn_off));
    }

    @Override
    public void showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_task_message));
    }

    @Override
    public void showLoadingTasksError() {
        showMessage(getString(R.string.loading_tasks_error));
    }

    @Override
    public void showDisplayBySortLabel() {
        mDisplayingLabelView.setText(getResources().getString(R.string.label_sort));
    }

    @Override
    public void showDisplayByTimeLabel() {
        mDisplayingLabelView.setText(getResources().getString(R.string.label_time));
    }

    private void showNoTasksViews(String mainText,int iconRes,boolean showAddView){
        mTasksView.setVisibility(View.GONE);
        mNoTaskView.setVisibility(View.VISIBLE);

        mNoTaskMainView.setText(mainText);
        mNoTaskIcon.setImageDrawable(getResources().getDrawable(iconRes));
        mNoTaskAddView.setVisibility(showAddView ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showDisplayingPopUpMenu() {
        PopupMenu popup = new PopupMenu(getContext(), getActivity().findViewById(R.id.menu_display));
        popup.getMenuInflater().inflate(R.menu.display_tasks, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.display_by_sort:
                        mPresenter.setDisplay(TasksDisplayType.TASKS_BY_SORT);
                        break;
                    case R.id.display_by_time:
                        mPresenter.setDisplay(TasksDisplayType.TASKS_BY_TIME);
                        break;
                }
                mPresenter.loadTasks(false);
                return true;
            }
        });
        popup.show();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    /**
     * Listener for clicks on tasks in the ListView.
     */
    TaskItemListener mItemListener = new TaskItemListener() {
        @Override
        public void onSortClick(List<Task>tasks,long sortId) {
            mListAdapter.replaceData(mPresenter.openCloseSort(tasks,sortId));
        }

        @Override
        public void onTaskClick(Task clickedTask) {
            mPresenter.openTaskDetails(clickedTask);
        }

        @Override
        public void onTurnOnTaskClick(Task turnOnTask) {
            mPresenter.turnOnTask(turnOnTask);
        }

        @Override
        public void onTurnOffTaskClick(Task turnOffTask) {
            mPresenter.turnOffTask(turnOffTask);
        }
    };

    @Override
    public void setPresenter(@NonNull TasksContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
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
        public View getView(final int i, View view, final ViewGroup viewGroup) {
            View rowView = view;
            if (rowView == null){
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                rowView = inflater.inflate(R.layout.task_item,viewGroup,false);
            }

            final Task task = getItem(i);

            TextView titleTV = rowView.findViewById(R.id.title);
            titleTV.setText(task.getTitle());

            CheckBox turnOnCB = rowView.findViewById(R.id.turn_on);

            if (!Objects.equal(task.getId(),(long)-1)) {
                //打开/关闭提醒UI
                turnOnCB.setChecked(task.isTurnOn());
                if (task.isTurnOn()) {
                    rowView.setBackgroundDrawable(viewGroup.getContext()
                            .getResources().getDrawable(R.drawable.list_turn_on_touch_feedback));
                } else {
                    rowView.setBackgroundDrawable(viewGroup.getContext()
                            .getResources().getDrawable(R.drawable.touch_feedback));
                }

                turnOnCB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!task.isTurnOn()) {
                            mItemListener.onTurnOnTaskClick(task);
                        } else {
                            mItemListener.onTurnOffTaskClick(task);
                        }
                    }
                });
            }else{
                turnOnCB.setChecked(false);
                turnOnCB.setEnabled(true);
                rowView.setBackgroundDrawable(viewGroup.getContext()
                        .getResources().getDrawable(R.drawable.touch_feedback));
            }
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Objects.equal(task.getId(),(long)-1)){
                        mItemListener.onSortClick(mTasks,task.getSortId());
                    }else {
                        mItemListener.onTaskClick(task);
                    }
                }
            });

            return rowView;
        }
    }

    public interface TaskItemListener{

        void onSortClick(List<Task>tasks,long sortId);

        void onTaskClick(Task clickTask);

        void onTurnOnTaskClick(Task TurnOnTask);

        void onTurnOffTaskClick(Task TurnOffTask);
    }
}
