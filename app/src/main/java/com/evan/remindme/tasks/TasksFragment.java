package com.evan.remindme.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.*;
import com.evan.remindme.R;
import com.evan.remindme.addedittask.AddEditTaskActivity;
import com.evan.remindme.allclassify.domain.model.Classify;
import com.evan.remindme.nexttimelistener.NextTimeListener;
import com.evan.remindme.taskdetail.TaskDetailActivity;
import com.evan.remindme.tasks.domain.model.Task;
import com.evan.remindme.util.DateUtils;

import java.util.*;

import static com.evan.remindme.addedittask.TasksCircleType.TASKS_CIRCLE_TYPE_LIST;
import static com.evan.remindme.addedittask.TasksRepeatType.TASKS_REPEAT_TYPE_LIST;
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

    private TasksExpandableListAdapter mExpandableListAdapter;

    private LinearLayout mNoTaskView;
    private ImageView mNoTaskIcon;
    private TextView mNoTaskMainView;
    private TextView mNoTaskAddView;

    private View mEmptyView;
    private LinearLayout mTasksView;

    private ScrollChildSwipeRefreshLayout swipeRefreshLayout;

    private ListView listView;
    private ExpandableListView expandableListView;

    public TasksFragment(){
        //需要空的公共构造函数
    }

    public static TasksFragment newInstance(){
        return new TasksFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mListAdapter = new TasksAdapter(new ArrayList<Task>(0),mItemListener);
        mExpandableListAdapter = new TasksExpandableListAdapter(new HashMap<Classify,List<Task>>(0),mItemListener);
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //设置浮动按钮
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_add);
        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.addNewTask();
            }
        });
    }

    public static View setEmptyView(Context context,View view, int dimenId){
        if(view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                viewGroup.getChildAt(i).setVisibility(View.GONE);
            }
        }
        int height = context.getResources().getDimensionPixelOffset(dimenId);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        view.setLayoutParams(params);
        return view;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.tasks_frag,container,false);

        mEmptyView = inflater.inflate(R.layout.task_item,null);
        mEmptyView = setEmptyView(getActivity(),mEmptyView,R.dimen.task_empty);

        //设置Tasks界面
        listView = root.findViewById(R.id.tasks_list);
        listView.addFooterView(mEmptyView,null,false);
        listView.setAdapter(mListAdapter);
        expandableListView = root.findViewById(R.id.expandable_tasks_list);
        expandableListView.addFooterView(mEmptyView,null,false);
        expandableListView.setAdapter(mExpandableListAdapter);
//        mDisplayingLabelView = root.findViewById(R.id.displayingLabel);
        mTasksView = root.findViewById(R.id.tasksLL);

        //设置没有提醒时的界面
        mNoTaskView = root.findViewById(R.id.noTasks);
        mNoTaskIcon = root.findViewById(R.id.noTasksIcon);
        mNoTaskMainView = root.findViewById(R.id.noTasksMain);
        mNoTaskAddView = root.findViewById(R.id.noTasksAdd);
        mNoTaskAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.addNewTask();
            }
        });

        //设置进度指示器
//        final ScrollChildSwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.refresh_layout);
        swipeRefreshLayout = root.findViewById(R.id.refresh_layout);

        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );

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
    public void showClassifyTasks(Map<Classify,List<Task>> tasks) {
        mExpandableListAdapter.replaceData(tasks);
        //在自定义的SwipeRefreshLayout中设置滚动视图
        swipeRefreshLayout.setScrollUpChild(expandableListView);
        expandableListView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        showTasks();
    }

    @Override
    public void showTimeTasks(List<Task> tasks) {
        mListAdapter.replaceData(tasks);
        //在自定义的SwipeRefreshLayout中设置滚动视图
        swipeRefreshLayout.setScrollUpChild(listView);
        expandableListView.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        showTasks();
    }

    private void showTasks(){
        mTasksView.setVisibility(View.VISIBLE);
        mNoTaskView.setVisibility(View.GONE);
    }

    @Override
    public void showAddTask() {
        Intent intent = new Intent(getContext(), AddEditTaskActivity.class);
        startActivityForResult(intent, AddEditTaskActivity.REQUEST_ADD_TASK);
    }

    @Override
    public void showTaskDetailsUi(String taskId) {
        //在它自己的Activity中，因为这样做更有意义，它使我们可以灵活地显示一些Intent存根。
        Intent intent = new Intent(getContext(), TaskDetailActivity.class);
        intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, taskId);
        startActivity(intent);
    }

    @Override
    public void showNoTasks() {
        showNoTasksViews(
                getResources().getString(R.string.no_tasks_all),
                R.drawable.ic_assignment_turned_in_24dp,
                true
        );
    }

    @Override
    public void showNoClassifyTasks() {
        showNoTasksViews(
                getResources().getString(R.string.no_tasks_all),
                R.drawable.ic_assignment_turned_in_24dp,
                true
        );
    }

    @Override
    public void showMessage(String message){
        Snackbar.make(Objects.requireNonNull(getView()),message,Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showTaskMarkedTurnOn() {
        Intent intent = new Intent(getActivity(),NextTimeListener.class);
        getActivity().startService(intent);
        showMessage(getString(R.string.task_marked_turn_on));
    }

    @Override
    public void showTaskMarkedTurnOff() {
        Intent intent = new Intent(getActivity(),NextTimeListener.class);
        getActivity().startService(intent);
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
                    case R.id.display_by_classify:
                        mPresenter.setDisplay(TasksDisplayType.TASKS_BY_CLASSIFY);
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
     * 监听ListView中tasks的点击
     */
    ListItemListener mItemListener = new ListItemListener() {

        @Override
        public void openClassify(int groupId) {
            expandableListView.expandGroup(groupId);
        }

        @Override
        public void onClassifyClick(boolean isOpen, Classify clickClassify, int groupId) {
            if (isOpen){
                mPresenter.closeClassify(clickClassify);
                expandableListView.collapseGroup(groupId);
            }else{
                mPresenter.openClassify(clickClassify);
                expandableListView.expandGroup(groupId,true);
            }
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
        private ListItemListener mItemListener;

        public TasksAdapter(List<Task>tasks, ListItemListener itemListener){
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
            final Task task = getItem(i);
            if (rowView == null){
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                rowView = inflater.inflate(R.layout.task_item,viewGroup,false);
            }
            TextView titleTV = rowView.findViewById(R.id.title);
            titleTV.setText(task.getTitle());

            TextView captionTV = rowView.findViewById(R.id.caption);
            captionTV.setText("起始时间:"+new DateUtils(DateUtils.tasksFormat).Date2String(task.getTime())+"/"+
                    TASKS_CIRCLE_TYPE_LIST[task.getCircle()+1]+"/"+
                    TASKS_REPEAT_TYPE_LIST[task.getRepeat()+1]);

            Switch turnOnCB = rowView.findViewById(R.id.turn_on);

            //打开/关闭提醒UI
            turnOnCB.setChecked(task.isTurnOn());

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
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        mItemListener.onTaskClick(task);
                }
            });

            return rowView;
        }
    }

    private static class TasksExpandableListAdapter extends BaseExpandableListAdapter{

        private Map<Classify,List<Task>> mTasks;
        private ListItemListener mItemListener;
        private List<Classify> mClassifies;

        public TasksExpandableListAdapter(Map<Classify,List<Task>>mTasks, ListItemListener itemListener){
            mClassifies = new ArrayList<>();
            setMap(mTasks);
            mItemListener = itemListener;
        }

        public void replaceData(Map<Classify,List<Task>>tasks){
            setMap(tasks);
            notifyDataSetChanged();
        }

        private void setMap(Map<Classify,List<Task>> tasks){
            mTasks = checkNotNull(tasks);
            mClassifies.clear();
            Collections.addAll(mClassifies,mTasks.keySet().toArray(new Classify[0]));
            Collections.sort(mClassifies);
            for (int i = 0; i < mClassifies.size(); i++) {
                if (mClassifies.get(i).isOpen()) {
                    mItemListener.openClassify(i);
                }
            }
        }

        //  获得父项的数量
        @Override
        public int getGroupCount() {
            return mTasks.size();
        }

        //  获得某个父项的子项数目
        @Override
        public int getChildrenCount(int i) {
            return mTasks.get(mClassifies.get(i)).size();
        }

        //  获得某个父项
        @Override
        public List<Task> getGroup(int i) {
            return mTasks.get(mClassifies.get(i));
        }

        //  获得某个父项的某个子项
        @Override
        public Task getChild(int i, int i1) {
            return mTasks.get(mClassifies.get(i)).get(i1);
        }

        //  获得某个父项的id
        @Override
        public long getGroupId(int i) {
            return i;
        }

        //  获得某个父项的某个子项的id
        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        //  按函数的名字来理解应该是是否具有稳定的id，这个方法目前一直都是返回false，没有去改动过
        @Override
        public boolean hasStableIds() {
            return false;
        }

        //  获得父项显示的view
        @Override
        public View getGroupView(final int i, final boolean b, View view, ViewGroup viewGroup) {
            View rowView = view;
            final Classify classify = mClassifies.get(i);
            if (rowView == null){
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                rowView = inflater.inflate(R.layout.classify_item,viewGroup,false);
            }
            int j = 0;
            for (Task task : getGroup(i)) {
                if (task.isTurnOn())j++;
            }
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemListener.onClassifyClick(b, classify ,i);
                }
            });
            TextView numTV = rowView.findViewById(R.id.num);
            numTV.setText(j+"/"+getChildrenCount(i));

            TextView titleTV = rowView.findViewById(R.id.title);
            titleTV.setText(classify.getName());
            return rowView;
        }

        //  获得子项显示的view
        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            View rowView = view;
            if (rowView == null){
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                rowView = inflater.inflate(R.layout.task_item,viewGroup,false);
            }

            final Task task = getChild(i,i1);

            TextView titleTV = rowView.findViewById(R.id.title);
            titleTV.setText(task.getTitle());

            TextView captionTV = rowView.findViewById(R.id.caption);
            captionTV.setText("起始时间:"+new DateUtils(DateUtils.tasksFormat).Date2String(task.getTime())+"/"+
                                TASKS_CIRCLE_TYPE_LIST[task.getCircle()+1]+"/"+
                                TASKS_REPEAT_TYPE_LIST[task.getRepeat()+1]);

            Switch turnOnCB = rowView.findViewById(R.id.turn_on);

            //打开/关闭提醒UI
            turnOnCB.setChecked(task.isTurnOn());
//            if (task.isTurnOn()) {
//                rowView.setBackgroundDrawable(viewGroup.getContext()
//                        .getResources().getDrawable(R.drawable.list_turn_on_touch_feedback));
//            } else {
//                rowView.setBackgroundDrawable(viewGroup.getContext()
//                        .getResources().getDrawable(R.drawable.touch_feedback));
//            }

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
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemListener.onTaskClick(task);
                }
            });

            return rowView;
        }

        //  子项是否可选中，如果需要设置子项的点击事件，需要返回true
        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }

    public interface ListItemListener{

        void openClassify(int groupId);

        void onClassifyClick(boolean isOpen, Classify clickClassify, int groupId);

        void onTaskClick(Task clickTask);

        void onTurnOnTaskClick(Task TurnOnTask);

        void onTurnOffTaskClick(Task TurnOffTask);
    }
}
