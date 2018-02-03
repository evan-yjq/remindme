package com.evan.remindme.addedittask;

import android.app.Activity;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.evan.remindme.R;
import com.evan.remindme.addedittask.domain.decorators.SelectDecorator;
import com.evan.remindme.addedittask.domain.decorators.TodayDecorator;
import com.evan.remindme.allclassify.AllClassifyFragment;
import com.evan.remindme.allclassify.domain.model.Classify;
import com.evan.remindme.tasks.ScrollChildSwipeRefreshLayout;
import com.evan.remindme.util.DateUtils;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.util.*;

import static android.content.Context.INPUT_METHOD_SERVICE;
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

    /**
     * 输入法管理器
     */
    private InputMethodManager mInputMethodManager;

    private AddEditTasksContract.Presenter mPresenter;
    private MaterialCalendarView mCalendarView;
    private EditText mTitleEditText;
    private TextView mTimeTextView;
    private Spinner mClassifySpinner;
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
                mPresenter.save(title);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.addtask_frag,container,false);

        mCalendarView = root.findViewById(R.id.calendar);
        mCalendarView.addDecorator(new TodayDecorator(getActivity()));
        mCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                calendar.setTime(mPresenter.getDate());
                calendar.set(date.getYear(),date.getMonth(),date.getDay());
                mPresenter.setDate(calendar.getTime());
                String day = new DateUtils().Date2String(calendar.getTime());
                mTimeTextView.setText(day);
                setSelectDecorator(date,mPresenter.getCircleType());
            }
        });

        mTitleEditText = root.findViewById(R.id.title);
        mTitleEditText.setHint("提醒名称");
        mTitleEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInput();
            }
        });

        mTimeTextView = root.findViewById(R.id.time);
        setDate(new Date());

        RelativeLayout mTimeView = root.findViewById(R.id.time_view);
        mTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.getDateDialog();
            }
        });

        mClassifySpinner = root.findViewById(R.id.classify_spinner);

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

        //初始化输入法
        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mInputMethodManager.isActive()) {
            mInputMethodManager.hideSoftInputFromWindow(mTitleEditText.getWindowToken(), 0);// 隐藏输入法
        }
    }

    private Calendar calendar = Calendar.getInstance();
    @Override
    public void showTimePickerDialog(Date date){
        calendar.setTime(date);
        new TimePickerDialog(getActivity(),5, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                calendar.set(Calendar.HOUR_OF_DAY,i);
                calendar.set(Calendar.MINUTE,i1);
                mPresenter.setDate(calendar.getTime());
                String day = new DateUtils().Date2String(calendar.getTime());
                mTimeTextView.setText(day);
            }
        },calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),true).show();
    }

    private void showInput(){
        mTitleEditText.setFocusable(true);
        mTitleEditText.setFocusableInTouchMode(true);//设置触摸聚焦
        mTitleEditText.requestFocus();//请求焦点
        mTitleEditText.findFocus();//获取焦点
        mInputMethodManager.showSoftInput(mTitleEditText, InputMethodManager.SHOW_FORCED);// 显示输入法
    }

    @Override
    public void hiddenInput(){
        mTitleEditText.setFocusable(false);
        if (mInputMethodManager.isActive()) {
            mInputMethodManager.hideSoftInputFromWindow(mTitleEditText.getWindowToken(), 0);// 隐藏输入法
        }
    }

    @Override
    public void setTitle(String title){
        mTitleEditText.setText(title);
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
    public void setSelectClassify(Classify classify) {
        ArrayAdapter<Classify> adapter = (ArrayAdapter<Classify>) mClassifySpinner.getAdapter();
        mClassifySpinner.setSelection(adapter.getPosition(classify),true);
    }

    @Override
    public void setSelectClassify(int i){
        mClassifySpinner.setSelection(i,true);
    }

    @Override
    public void setDate(Date date){
        calendar.setTime(date);
        mCalendarView.setSelectedDate(date);
        String day = new DateUtils().Date2String(date);
        mTimeTextView.setText(day);
    }

    @Override
    public void showTasksList() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    private void showAddClassify() {
        showDialog("新建分类",new AllClassifyFragment.DialogListener() {
            @Override
            public void onPositiveClick(EditText text) {
                String input = text.getText().toString();
                if (input.equals("")) {
                    showMessage("分类名不能为空");
                }else{
                    mPresenter.saveClassify(input);
                }
            }
        },"");
    }

    private void showDialog(String title, AllClassifyFragment.DialogListener listener, String hint){
        TextInputLayout layout = (TextInputLayout) setPadding(new TextInputLayout(getActivity()));
        EditText et = new EditText(getActivity());
        et.setSingleLine();
        et.setHint(hint);
        layout.addView(et);

        AllClassifyFragment.MyDialogFragment dialog = new AllClassifyFragment.MyDialogFragment(title,layout,listener);

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
    public void setClassifySpinner(final List<Classify> classifies){
        classifies.add(new Classify("点击这里新建分类+"));
        ArrayAdapter<Classify> adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_dropdown_item, classifies);
        mClassifySpinner.setAdapter(adapter);
        mClassifySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == classifies.size()-1){
                    showAddClassify();
                }else {
                    mPresenter.setClassify(((ArrayAdapter<Classify>)adapterView.getAdapter()).getItem(i));
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
                setSelectDecorator(mCalendarView.getSelectedDate(),i-1);
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
    private void setSelectDecorator(CalendarDay day,int circleType){
        if (selectDecorator!=null) {
            mCalendarView.removeDecorator(selectDecorator);
        }
        selectDecorator =  new SelectDecorator(day,getActivity(),circleType);
        mCalendarView.addDecorator(selectDecorator);
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
