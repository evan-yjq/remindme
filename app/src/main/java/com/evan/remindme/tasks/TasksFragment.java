package com.evan.remindme.tasks;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.view.MenuItem;
import android.widget.PopupMenu;
import com.evan.remindme.R;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/24
 * Time: 下午8:08
 */
public class TasksFragment extends Fragment implements TasksContract.View {

    private TasksContract.Presenter mPresenter;

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
}
