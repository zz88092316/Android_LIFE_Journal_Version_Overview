package com.example.lenovo.Android_LIFE_Journal;import android.app.Activity;import android.content.ContentResolver;import android.content.Intent;import android.content.res.AssetManager;import android.graphics.Bitmap;import android.graphics.BitmapFactory;import android.graphics.Typeface;import android.net.Uri;import android.os.Build;import android.os.Bundle;import android.os.Environment;import android.os.Message;import android.os.StrictMode;import android.provider.MediaStore;import android.support.v4.content.FileProvider;import android.text.Editable;import android.text.Spannable;import android.text.SpannableString;import android.text.style.ImageSpan;import android.util.Log;import android.view.KeyEvent;import android.view.Menu;import android.view.MenuItem;import android.view.View;import android.view.Window;import android.widget.Button;import android.widget.EditText;import android.widget.ScrollView;import android.widget.Toast;import com.example.lenovo.Android_LIFE_Journal.function.NoteWrapper;import com.example.lenovo.Android_LIFE_Journal.function.StringFunction;import com.example.lenovo.Android_LIFE_Journal.model.NoteInfo;import com.example.lenovo.Android_LIFE_Journal.ui.MessageBox;import com.example.lenovo.Android_LIFE_Journal.ui.UiHelper;import java.io.ByteArrayOutputStream;import java.io.File;import java.io.FileInputStream;import java.io.FileNotFoundException;import java.io.InputStream;import java.util.Calendar;import java.util.Locale;public class edit_activity extends Activityimplements NoteWrapper.INoteInfo{    private ScrollView scrollView;    private EditText editText;    private Button gallery_btn;    private Button photo_btn;    private NoteInfo noteInfo;    private Intent intent;    private String originalContent=null;    private NoteWrapper noteWrapper;    public static final String IMAGEFilePATH="/sdcard/myImage/";    private String picpath;    private String content;    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.edit_activity);     // getWindow().setBackgroundDrawableResource(R.drawable.timg);        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();            StrictMode.setVmPolicy(builder.build());        }        // 通過自定義字體生成字體對象        AssetManager mgr=this.getAssets();        Typeface tf=Typeface.createFromAsset(mgr, "fonts/new.ttf");        scrollView=(ScrollView)findViewById(R.id.scrollView);        editText=(EditText)findViewById(R.id.content);        gallery_btn=(Button)findViewById(R.id.picture);        photo_btn=(Button)findViewById(R.id.photo);        editText.setTypeface(tf);        picpath=IMAGEFilePATH+ "temp.png";        /*        從相簿獲取圖片         */        gallery_btn.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);                getImage.addCategory(Intent.CATEGORY_OPENABLE);                getImage.setType("image/*");                startActivityForResult(getImage, 0x111);            }        });        /*        拍照獲取圖片        這裡選擇通過相機拍照，先將原圖片儲存到一個固定的 picpath，然後傳遞 Uri         */        photo_btn.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);                Uri uri = Uri.fromFile(new File(picpath));                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);                startActivityForResult(intent, 0x222);            }        });        Bundle bundle = getIntent().getExtras();        noteWrapper=new NoteWrapper((NoteWrapper.INoteInfo) edit_activity.this,edit_activity.this);        // 獲取到 NoteInfo 對象，如果是 null 說明這是新建的筆記        noteInfo=(NoteInfo)bundle.getSerializable("flag");        if(noteInfo!=null) {            //獲取圖文內容            SpannableString ss=noteWrapper.getSpannedString(noteInfo.getContent(),                    edit_activity.this);            editText.setText(ss);            originalContent = noteInfo.getContent();        }    }    @Override    protected void onActivityResult(int requestCode, int resultCode, Intent data) {        super.onActivityResult(requestCode, resultCode, data);        super.onActivityResult(requestCode, resultCode, intent);        ContentResolver resolver=getContentResolver();        /*        如果是從圖庫獲取的圖片         */        if(requestCode==0x111&&resultCode==RESULT_OK){            Uri originalUri=data.getData();            Bitmap ori_bitmap = null;            Bitmap ori_rbitmap = null;            //通過圖庫傳來的Uri解析、壓縮圖片            try {                ori_bitmap= BitmapFactory.decodeStream(resolver.openInputStream(originalUri),null,                        noteWrapper.getBili(480,800));                ori_bitmap=noteWrapper.jinyibu(ori_bitmap,edit_activity.this);            } catch (FileNotFoundException e) {                e.printStackTrace();            }            ori_rbitmap=ori_bitmap;            String sdStatus = Environment.getExternalStorageState();            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 檢測 SD 是否可用                Log.i("TestFile","SD card is not avaiable/writeable right now.");            }            // 將圖片保存到新的文件並返迴原路徑            String myPath =noteWrapper.SaveToFile(IMAGEFilePATH,                    Calendar.getInstance(Locale.CHINA).getTimeInMillis() + ".jpg",                    ori_rbitmap);            //將圖片插入到EditText中的操作            Log.w(myPath,"fileName");            SpannableString span_str = new SpannableString(myPath);            Bitmap my_bm=BitmapFactory.decodeFile(myPath);            Bitmap my_rbm=my_bm;            ImageSpan span = new ImageSpan(this, my_rbm);            span_str.setSpan(span, 0, myPath.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);            Editable et = editText.getText();// 先獲取 Edittext 中的內容            int start = editText.getSelectionStart();            SpannableString newLine = new SpannableString("\n");            et.insert(start,newLine);            start = editText.getSelectionStart();            et.insert(start,newLine);            start = editText.getSelectionStart();            et.insert(start, span_str);// 設置 ss 要添加的位置            start = editText.getSelectionStart();            et.insert(start,newLine);            editText.setText((CharSequence)et);// 把 et 添加到Edittext中            editText.setSelection(start);// 設置 Edittext 中,標在最後面        }        /*        如果是從照相機獲取的圖片         */        if(requestCode==0x222&&resultCode==RESULT_OK){            String sdStatus = Environment.getExternalStorageState();            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 檢測 SD 是否可用                Log.i("TestFile","SD card is not avaiable/writeable right now.");            }            String name = Calendar.getInstance(Locale.CHINA).getTimeInMillis() + ".jpg";// 給拍的照片命名，之後進行存儲            // 將拍照獲得的圖片壓縮 , 防止 OOM            Bitmap newb=BitmapFactory.decodeFile(picpath, noteWrapper.getBili(480,800));            Bitmap camera_rbitmap=noteWrapper.jinyibu(newb,edit_activity.this);            //將圖片保存到新的文件並返回原路徑            String myPath =noteWrapper.SaveToFile(IMAGEFilePATH,                    Calendar.getInstance(Locale.CHINA).getTimeInMillis() + ".jpg",                    camera_rbitmap);            //將圖片插入到 EditText 中            Log.w(myPath,"fileName");            SpannableString span_str = new SpannableString(myPath);            Bitmap my_bm=BitmapFactory.decodeFile(myPath);            Bitmap my_rbm=my_bm;            ImageSpan span = new ImageSpan(this, my_rbm);            span_str.setSpan(span, 0, myPath.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);            Editable et = editText.getText();// 先獲取 Edittext 中的內容            int start = editText.getSelectionStart();            SpannableString newLine = new SpannableString("\n");            et.insert(start,newLine);            start = editText.getSelectionStart();            et.insert(start,newLine);            start = editText.getSelectionStart();            et.insert(start, span_str);// 設置 ss 要添加的位置            start = editText.getSelectionStart();            et.insert(start,newLine);            editText.setText((CharSequence)et);// 把et添加到Edittext中            editText.setSelection(start);// 設置Edittext中 , 標在最後面        }    }        /*        創建菜單         */        @Override        public boolean onCreateOptionsMenu(Menu menu) {            getMenuInflater().inflate(R.menu.menu_main,menu);//getMenuInflater()方法得到MenuInflater            //調用inflate接收兩個參數            //R.menu.main指調用menu文件下的main資源文件            return true;//返回true，允許創建的菜單顯示,返回false不顯示        }    //定義菜單響應事件    @Override    public boolean onOptionsItemSelected(MenuItem item) {        switch (item.getItemId())        {            // 菜單欄“保存”選項            case R.id.context_menu_save:                // 獲取最新編輯框的內容                 content=editText.getText().toString();                if(noteInfo==null&& StringFunction.isNotNullOrEmpty(content)) {// 如果是新的筆記且內容不能空白                  noteWrapper.SaveToDB(content, edit_activity.this,noteInfo);                    //originalContent=noteInfo.getContent();                }else if(noteInfo!=null&&// 不是一個新的筆記                        !originalContent.equals(content)&&// 與原文內容不一樣                        StringFunction.isNotNullOrEmpty(content)){// 內容不能空白                  noteWrapper.SaveToDB(content, edit_activity.this,noteInfo);                }                originalContent=editText.getText().toString();                break;                // 重命名選項            case R.id.context_menu_rename:                if(noteInfo==null){                    UiHelper.toastShowMessageShort(edit_activity.this,"此筆記未儲存");                }else{                    noteWrapper.noteRename(edit_activity.this,noteInfo);                }                break;            // 菜單欄“刪除”選項            case   R.id.context_menu_delete:                 content=editText.getText().toString();                if(noteInfo==null||(checkByContent(content))){                    UiHelper.toastShowMessageShort(edit_activity.this,"筆記未儲存，無法刪除");                }else{                    noteWrapper.deleteNote(noteInfo,edit_activity.this);                }                break;                /*                分享功能，調用系統的分享模板，發送到任何可以接受的應用                 */            case R.id.context_menu_share:                content=editText.getText().toString();                if(noteInfo==null||(checkByContent(content))){                    UiHelper.toastShowMessageShort(edit_activity.this,"筆記未儲存，無法分享");                }else {                    Bitmap bitmap = noteWrapper.compressImage(noteWrapper.getScrollViewBitmap(scrollView, null));                    File file = noteWrapper.bitMap2File(bitmap);                    if (file != null && file.exists() && file.isFile()) {                        // 由文件得到uri                        Uri imageUri = Uri.fromFile(file);                        Intent shareIntent = new Intent();                        shareIntent.setAction(Intent.ACTION_SEND);                        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);                        shareIntent.setType("image/*");                        startActivity(Intent.createChooser(shareIntent, "分享圖片"));                    }                }                break;        }        return true;    }    @Override    public void getANewNote(NoteInfo noteInfos) {        this.noteInfo=noteInfos;    }    /*    監聽返回鍵事件    如果筆記未儲存,提醒用戶    */    @Override    public boolean onKeyDown(int keyCode, KeyEvent event) {        if(keyCode==KeyEvent.KEYCODE_BACK){           content=editText.getText().toString();            if(checkByContent(content)){                MessageBox messageBox=new MessageBox(edit_activity.this);                messageBox.showOKOrCancelDialog("此筆記未儲存，是否返回", "提示",new MessageBox.IButtonClick() {                    @Override                    public void doSomething() {                        finish();                    }                },null);            }        }         return super.onKeyDown(keyCode, event);    }    /*    根據現在內容和原始內容來判斷是否可以進行下一步操作     */        public boolean checkByContent(String content){            if((StringFunction.isNotNullOrEmpty(content)&&                    StringFunction.isNotNullOrEmpty(originalContent)&&                    !originalContent.equals(content))                    ||                    ( !StringFunction.isNotNullOrEmpty(originalContent)                            &&StringFunction.isNotNullOrEmpty(content))                    )                return true;            else return false;        }}