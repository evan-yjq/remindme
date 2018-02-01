package com.evan.remindme.addedittask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.evan.remindme.R;
import com.evan.remindme.addedittask.domain.decorators.NextDecorator;
import com.evan.remindme.addedittask.domain.decorators.SelectDecorator;
import com.evan.remindme.addedittask.domain.decorators.TodayDecorator;
import com.evan.remindme.sorts.SortsFragment;
import com.evan.remindme.sorts.domain.model.Sort;
import com.evan.remindme.tasks.ScrollChildSwipeRefreshLayout;
import com.evan.remindme.tasks.domain.model.Task;
import com.evan.remindme.util.DateUtils;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.util.*;

import static com.evan.remindme.addedittask.TasksCircleType.TASKS_CIRCLE_TYPE_LIST;
import static com.evan.remindme.addedittask.TasksRepeatType.TASKS_REPEAT_TYPE_LIST;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/2/1
 * Time: 下午5:10
 */
public class AddEditTaskFragment extends Fragment implements AddEditTasksContract.View{

    public static final String ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID";

    private AddEditTasksContract.Presenter mPresenter;
    private MaterialCalendarView mCalendarView;
    private EditText mTitleEditText;
    private TextView mDayTextView;
    private TextView mTimeTextView;
    private Spinner mSortSpinner;
    private Spinner mCircleSpinner;
    private Spinner mRepeatSpinner;

    public AddEditTaskFragment(){}

    public static AddEditTaskFragment newInstance(){
        return new AddEditTaskFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_edit_task_done);
        fab.setImageResource(R.drawable.ic_done);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = mTitleEditText.getText().toString();
                Date date = null;
                if (day != null&&time != null){
                    try {
                        date = new DateUtils().String2Date(day + " " + time);
                    } catch (ParseException e) {
                        showMessage("解析时间出错");
                    }
                }
                mPresenter.save(title,date);

            }
        });
    }

    private String day = null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.addtask_frag,container,false);

        mCalendarView = root.findViewById(R.id.calendar);
        mCalendarView.addDecorator(new TodayDecorator(getActivity()));
        mCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                calendar.setTime(date.getDate());
                day = calendar.get(Calendar.YEAR)+"年"+(calendar.get(Calendar.MONTH)+1)+"月"+calendar.get(Calendar.DAY_OF_MONTH)+"日";
                mDayTextView.setText(day);
                setCalendarDecorators(date,mPresenter.getCircleType());
            }
        });

        mTitleEditText = root.findViewById(R.id.title);

        mDayTextView = root.findViewById(R.id.day);
        mTimeTextView = root.findViewById(R.id.time);

        RelativeLayout mTimeView = root.findViewById(R.id.time_view);
        mTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.getDateDialog();
            }
        });

        mSortSpinner = root.findViewById(R.id.sort_spinner);

        mCircleSpinner = root.findViewById(R.id.circle_spinner);
        setCircleSpinner();

        mRepeatSpinner = root.findViewById(R.id.repeat_spinner);
        setRepeatSpinner();


        final ScrollChildSwipeRefreshLayout srl = root.findViewById(R.id.refresh_layout);
        srl.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        ScrollView layout = root.findViewById(R.id.addeditLL);
        srl.setScrollUpChild(layout);
        srl.setEnabled(false);
        return root;
    }

    private String time = null;
    private Calendar calendar = Calendar.getInstance();
    @Override
    public void showTimePickerDialog(Date date){
        calendar.setTime(date);
        new TimePickerDialog(getActivity(),5, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                time = i+"时"+i1+"分";
                mTimeTextView.setText(time);
            }
        },calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),true).show();
    }

    @Override
    public void setTitle(String title){
        mTitleEditText.setHint(title);
    }

    @Override
    public void setSelectRepeat(int repeat) {
        mRepeatSpinner.setSelection(repeat+1,true);
    }

    @Override
    public void setSelectCircle(int circle) {
        mCircleSpinner.setSelection(circle+1,true);
    }

    @Override
    public void setSelectSort(Sort sort) {
        ArrayAdapter<Sort> adapter = (ArrayAdapter<Sort>) mSortSpinner.getAdapter();
        mSortSpinner.setSelection(adapter.getPosition(sort),true);
    }

    @Override
    public void setSelectSort(int i){
        mSortSpinner.setSelection(i,true);
    }

    @Override
    public void setDate(Date date){
        calendar.setTime(date);
        mCalendarView.setSelectedDate(date);
        day = calendar.get(Calendar.YEAR)+"年"+(calendar.get(Calendar.MONTH)+1)+"月"+calendar.get(Calendar.DAY_OF_MONTH)+"日";
        time = calendar.get(Calendar.HOUR_OF_DAY)+"时"+calendar.get(Calendar.MINUTE)+"分";
        mDayTextView.setText(day);
        mTimeTextView.setText(time);
        setCalendarDecorators(CalendarDay.from(date),mPresenter.getCircleType());
    }

    private void setCalendarDecorators(CalendarDay day,int circle){
        setSelectCalendar(day,circle);
        setNextCalendar(day, circle);
    }

    @Override
    public void showTasksList() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    public void showAddSort() {
        showDialog("新建分类",new SortsFragment.DialogListener() {
            @Override
            public void onPositiveClick(EditText text) {
                String input = text.getText().toString();
                if (input.equals("")) {
                    showMessage("分类名不能为空");
                }else{
                    mPresenter.saveSort(input);
                }
            }
        },"");
    }

    private void showDialog(String title, SortsFragment.DialogListener listener, String hint){
        TextInputLayout layout = (TextInputLayout) setPadding(new TextInputLayout(getActivity()));
        EditText et = new EditText(getActivity());
        et.setSingleLine();
        et.setHint(hint);
        layout.addView(et);

        SortsFragment.MyDialogFragment dialog = new SortsFragment.MyDialogFragment(title,layout,listener);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        dialog.show(ft,"Dialog");
    }

    @NonNull
    private View setPadding(View v){
        v.setPaddingRelative((int)(getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin)*1.3),
                getResources().getDimensionPixelOffset(R.dimen.activity_vertical_margin),
                (int)(getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin)*1.3),
                getResources().getDimensionPixelOffset(R.dimen.activity_vertical_margin));
        return v;
    }

    @Override
    public void setSortSpinner(final List<Sort>sorts){
        sorts.add(new Sort("点击这里新建分类+"));
        ArrayAdapter<Sort> adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_dropdown_item,sorts);
        mSortSpinner.setAdapter(adapter);
        mSortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == sorts.size()-1){
                    showAddSort();
                }else {
                    mPresenter.setSortId(((ArrayAdapter<Sort>)adapterView.getAdapter()).getItem(i).getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setCircleSpinner(){
        List<String>circles = new ArrayList<>();
        Collections.addAll(circles,TASKS_CIRCLE_TYPE_LIST);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_dropdown_item,circles);
        mCircleSpinner.setAdapter(adapter);
        mCircleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mPresenter.setCircleType(i-1);
                if (mCalendarView.getSelectedDate()==null)
                    return;
                setCalendarDecorators(mCalendarView.getSelectedDate(),i-1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setRepeatSpinner(){
        List<String>repeats = new ArrayList<>();
        Collections.addAll(repeats,TASKS_REPEAT_TYPE_LIST);
        ArrayAdapter<String>adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_dropdown_item,repeats);
        mRepeatSpinner.setAdapter(adapter);
        mRepeatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mPresenter.setRepeatType(i-1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private SelectDecorator selectDecorator;
    private void setSelectCalendar(CalendarDay day,int circleType){
        if (selectDecorator!=null) {
            mCalendarView.removeDecorator(selectDecorator);
        }
        selectDecorator =  new SelectDecorator(day,getActivity(),circleType);
        mCalendarView.addDecorator(selectDecorator);
    }

    private NextDecorator nextDecorator;
    private void setNextCalendar(CalendarDay day,int circleType){
        if (nextDecorator!=null){
            mCalendarView.removeDecorator(nextDecorator);
        }
        nextDecorator = new NextDecorator(day,getActivity(),circleType);
        mCalendarView.addDecorator(nextDecorator);
    }

    @Override
    public void setLoadingIndicator(final boolean active) {
        if (getView() == null)return;
        final ScrollChildSwipeRefreshLayout srl = getView().findViewById(R.id.refresh_layout);
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
    public void showMessage(String message) {
        Snackbar.make(getView(),message,Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void setPresenter(AddEditTasksContract.Presenter presenter) {
        this.mPresenter = presenter;
    }
}
