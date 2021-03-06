package com.example.rsreu_app;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.rsreu_app.model.Day;
import com.example.rsreu_app.model.Lesson;
import com.levitnudi.legacytableview.LegacyTableView;

import java.util.Date;

import static com.levitnudi.legacytableview.LegacyTableView.CUSTOM;

/**
 * PageFragment отображает информацию по 1 дню,
 * как правило вызов происходит из MyPageAdapter,
 * в котором хранится неделя
 */
public class PageFragment extends Fragment {
    private static final int L_WIDTH_SCREEN = 1000;
    private static final int M_WIDTH_SCREEN = 900;
    private static final int S_WIDTH_SCREEN = 800;
    private static final String DAY_KEY = "day_key_PageFragment";
    private static final String MODE_KEY = "mode_key_PageFragment";
    private static final int NUM_COLUMNS = 4;

    private LegacyTableView mTableView;
    /**
     * 1 - нет расписания по причине выходного дня
     * 2 - нет расписания, т.к. закончился семестр
     */
    private int mode;
    public static final int MODE_WEEKEND = 1;
    public static final int MODE_NOT_DATA_FROM_SEMESTER = 2;
    private Day mDay;


    public static PageFragment newInstance(Day day) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putSerializable(DAY_KEY, day);
        fragment.setArguments(args);
        return fragment;
    }

    public static PageFragment newInstance(Day day, int mode) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putSerializable(DAY_KEY, day);
        args.putSerializable(MODE_KEY, mode);
        fragment.setArguments(args);
        return fragment;
    }

    public PageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);

        if (getArguments() != null) {
            mDay = (Day) getArguments().getSerializable(DAY_KEY);
        } else {
            mDay = null;
        }
        if (getArguments().getInt(MODE_KEY) != 0) {
            mode = getArguments().getInt(MODE_KEY);
        } else {
            mode = 0;
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        mTableView = view.findViewById(R.id.time_table_view);
        if (mode == 0) {
            if (width < M_WIDTH_SCREEN) {
                LegacyTableView.insertLegacyTitle(
                        getString(R.string.time_lesson),
                        getString(R.string.name_lesson),
                        getString(R.string.audience),
                        getString(R.string.teacher)
                );
                int padding = Integer.valueOf(getActivity().getString(R.string.padding_table_s));
                mTableView.setTablePadding(padding);
            } else {
                LegacyTableView.insertLegacyTitle(
                        getString(R.string.time_lesson),
                        getString(R.string.name_lesson),
                        getString(R.string.audience),
                        getString(R.string.teacher)
                );
                int padding = Integer.valueOf(getActivity().getString(R.string.padding_table_m));
                mTableView.setTablePadding(padding);
            }
            LegacyTableView.insertLegacyContent(getStringArrayContentTable());
        } else if (mode == MODE_WEEKEND) {
            LegacyTableView.insertLegacyContent(getActivity().getString(R.string.message_no_lessons));
        } else if (mode == MODE_NOT_DATA_FROM_SEMESTER) {
            LegacyTableView.insertLegacyContent(getActivity().getString(R.string.message_not_date_from_semester));
        }
        if (width > L_WIDTH_SCREEN) {
            int margin = Integer.valueOf(getActivity().getString(R.string.margin_s));
            mTableView.setPadding(margin, 0, margin, 0);
            int padding = Integer.valueOf(getActivity().getString(R.string.padding_table_l));
            mTableView.setTablePadding(padding);
        }


        String array[] = new String[16];
        for (int i = 0; i < array.length; i++) {
            array[i] = Integer.toString(i + 1);
        }


        mTableView.setTitle(LegacyTableView.readLegacyTitle());
        mTableView.setContent(LegacyTableView.readLegacyContent());
        mTableView.setHorizontalScrollBarEnabled(false);
        mTableView.setTheme(CUSTOM);
        mTableView.setBackgroundEvenColor(getActivity().getString(R.string.colorDarkBlue));
        mTableView.setContentFont(1);
        mTableView.setBottomShadowColorTint(getActivity().getString(R.string.colorDarkBlue));
        mTableView.setTitleFont(1);
        if (mode == 0){
            mTableView.setContentTextSize(Integer.valueOf(getActivity().getString(R.string.size_table_content_l)));
            mTableView.setTitleTextSize(Integer.valueOf(getActivity().getString(R.string.size_table_title_l)));
        }else {
            mTableView.setContentTextSize(Integer.valueOf(getActivity().getString(R.string.size_table_content_xl)));
            mTableView.setTitleTextSize(Integer.valueOf(getActivity().getString(R.string.size_table_title_xl)));
        }

//        mTableView.setMinimumWidth(width);
        mTableView.setBackgroundOddColor("#536DFE");
        mTableView.setBackgroundEvenColor("#536DFE");
        mTableView.setHeaderBackgroundLinearGradientBOTTOM(getActivity().getString(R.string.colorLightBlue));
        mTableView.setHeaderBackgroundLinearGradientTOP(getActivity().getString(R.string.colorLightBlue));
        mTableView.setTitleTextColor(getActivity().getString(R.string.colorDarkBlue));
        mTableView.setContentTextColor(getActivity().getString(R.string.colorDarkBlue));

        mTableView.setContentTextAlignment(2);
        mTableView.setTitleTextAlignment(2);
        mTableView.build();
        mTableView.getHeight();

        return view;
    }


    private String getAnyColumnValueExample(Context context, int weekDay, int timeId, int weekBool, String columnYouWant) {
        DatabaseHelper myDB;
        myDB = new DatabaseHelper(context);
        int groupNumber = 543;
        Cursor c = myDB.getInfo(groupNumber, weekDay, timeId, weekBool);
        return c.getString(c.getColumnIndex("title"));  // columnYouWant вместо title
    }

    /**
     * Получение строкового массива,
     * который будет испльзоваться для заполнения полей таблицы
     *
     * @return строковый массив для контента таблицы.
     */
    private String[] getStringArrayContentTable() {
        if (mDay == null) {
            String[] strings = {};
            return strings;
        }
        String[] content = new String[mDay.getLessons().size() * NUM_COLUMNS];
        int index = 0;
        for (int i = 0; i < mDay.getLessons().size(); i++) {
            Lesson lesson = mDay.getLessons().get(i);
            content[index++] = lesson.getTimeFromTimeId(getActivity());
            content[index++] = lesson.getTitleTypeOptional();
            content[index++] = lesson.getRoomBuilding();
            if (lesson.getTeachers().equals("")){
                content[index++] = " ";
            } else {
                content[index++] = lesson.getTeachers();
            }


        }
        return content;

    }


}
