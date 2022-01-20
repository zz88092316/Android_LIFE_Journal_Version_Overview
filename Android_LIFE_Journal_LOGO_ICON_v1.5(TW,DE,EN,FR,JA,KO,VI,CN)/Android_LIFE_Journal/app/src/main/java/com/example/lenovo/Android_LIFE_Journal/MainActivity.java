package com.example.lenovo.Android_LIFE_Journal;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.Android_LIFE_Journal.adapter.DataAdapter;
import com.example.lenovo.Android_LIFE_Journal.db.DBdataSource;
import com.example.lenovo.Android_LIFE_Journal.function.SearchNoteAsyncTask;
import com.example.lenovo.Android_LIFE_Journal.model.NoteInfo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity
        extends Activity
        implements SearchNoteAsyncTask.IExplorerUIComponent {

    private  Typeface tf;
    private AutoCompleteTextView acTextView = null;
    private Intent intent = null;
    private Button addNote = null;
    private ListView noteList = null;
    private TextView emptyView;
    private DBdataSource dBdataSource = null;
    private SearchNoteAsyncTask searchNoteAsyncTask = null;
    private DataAdapter dataAdapter = null;
    private Bundle NoteFlag;//Bundle數據包，用來傳遞一個 NoteInfo 對象
    private NoteInfo noteInfo = null;
    private List<NoteInfo> list = null;
    //代表 ActionBar 進入了不同的狀態
    private ActionMode actionMode = null;
    ArrayAdapter<String> adapter = null;
    private  String nameData[]=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setBackgroundDrawableResource(R.drawable.timg);

        // 提醒用戶設置權限
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                    1);
        }

        // 獲取一個新字體  搜尋
        AssetManager mgr=MainActivity.this.getAssets();
        tf=Typeface.createFromAsset(mgr, "fonts/new.ttf");

        acTextView=(AutoCompleteTextView)findViewById(R.id.id_autotextView);
        addNote = (Button) findViewById(R.id.addNote);
        noteList = (ListView) findViewById(R.id.notelist);
        dBdataSource = new DBdataSource(this);

        /*
        添加筆記的按鈕設置點擊事件
         */
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 跳轉到編輯界面
                intent = new Intent(MainActivity.this, edit_activity.class);
                NoteFlag = new Bundle();
                NoteFlag.putSerializable("flag", noteInfo);// 該標記代表是否為新建的筆記，傳遞一個 NoteInfo 對象
                intent.putExtras(NoteFlag);
                startActivity(intent);
            }
        });

        /*
        設置 AutoCompleteTextView 點擊事件
         */
        acTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(MainActivity.this, edit_activity.class);
                // 該標記代表是否為新建的筆記，傳遞一個 NoteInfo 對象
                NoteFlag = new Bundle();
                String goalName=parent.getItemAtPosition(position).toString();
                NoteFlag.putSerializable("flag", dBdataSource.getByName(goalName));
                intent.putExtras(NoteFlag);
                startActivity(intent);
            }
        });

        /*
        添加 ListView 長按多選事件
         */
        noteList.setMultiChoiceModeListener(mActionModeCallback);

        /*
        添加 ListView 點擊事件
         */
        noteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 跳轉到編輯界面
                intent = new Intent(MainActivity.this, edit_activity.class);
                //該標記代表是否為新建的筆記，傳遞一個 NoteInfo 對象
                NoteFlag = new Bundle();
                NoteFlag.putSerializable("flag", (NoteInfo) parent.getAdapter().getItem(position));
                intent.putExtras(NoteFlag);
                startActivity(intent);
            }
        });

    }

      //刪除鍵 my_listview_ctx_menu

    private AbsListView.MultiChoiceModeListener mActionModeCallback = new AbsListView.MultiChoiceModeListener() {
        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            mode.setSubtitle("已選中：" + noteList.getCheckedItemCount());
        }

        // 當數據行的選中狀態發生改變時……
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // 實例化在 ActionBar 上顯示的上下文菜單
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.my_listview_ctx_menu, menu);

            // 使用setTitle()，用大標題顯示信息，使用setSubtitle，會在大標題下顯示小標題
            mode.setTitle("进入ActionMode");
            mode.setSubtitle("已選中：" + noteList.getCheckedItemCount());

            actionMode = mode;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                //用戶選擇刪除
                case R.id.action_delete:
                    // 獲取用戶選中的所有記錄
                    List<NoteInfo> objs = new ArrayList<>();
                    SparseBooleanArray array = noteList.getCheckedItemPositions();
                    for (int i = 0; i < array.size(); ++i) {
                        if (array.valueAt(i)) {
                            objs.add(list.get(array.keyAt(i)));
                        }
                    }
                    // 從數據源中刪除數據對象
                    for (NoteInfo obj : objs) {
                        dBdataSource.deleteById(obj.getId());
                        dataAdapter.deleteItem(obj);
                    }
                    // 更新顯示
                    dataAdapter.notifyDataSetChanged();
                    // 退出多選模式
                    mode.finish();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    };


        ;

        /*
        初始化 ListView
         */
        public void initListView() {
            // 執行異步查詢功能
            searchNoteAsyncTask = new SearchNoteAsyncTask(MainActivity.this, this);
            searchNoteAsyncTask.execute();
        }

        @Override
        protected void onResume() {
            super.onResume();
            dBdataSource.open();
            initListView();
        }

        @Override
        protected void onPause() {
            super.onPause();
            dBdataSource.close();
        }

        @Override
        public void showNoteInfos(List<NoteInfo> Info) {

            dataAdapter = new DataAdapter(MainActivity.this, Info,tf);
            list = Info;
            noteList.setAdapter(dataAdapter);
            setName();
            adapter = new ArrayAdapter<String>(MainActivity.this,
                    android.R.layout.simple_dropdown_item_1line, nameData);
            acTextView.setAdapter(adapter);
        }

        public void setName(){
            nameData= new String[list.size()];
            int i=0;
            for(NoteInfo noteInfo:list){
                nameData[i]=noteInfo.getName();
                i++;
            }
        }

    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}

