package com.example.lenovo.Android_LIFE_Journal.function;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.EditText;
import android.widget.ScrollView;

import com.example.lenovo.Android_LIFE_Journal.MainActivity;
import com.example.lenovo.Android_LIFE_Journal.R;
import com.example.lenovo.Android_LIFE_Journal.db.DBdataSource;
import com.example.lenovo.Android_LIFE_Journal.model.NoteInfo;
import com.example.lenovo.Android_LIFE_Journal.ui.MessageBox;
import com.example.lenovo.Android_LIFE_Journal.ui.UiHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
/*
封裝一些操作編輯器的方法
 */
public class NoteWrapper {



    /*
      此接口的作用是想 edit_activity   傳送一個新的 NoteInfo
    */
    public interface INoteInfo{

        void getANewNote(NoteInfo noteInfo);
    }

    public NoteWrapper (INoteInfo iNoteInfo,Context contexts) {
        this.iNoteInfo=iNoteInfo;
        context=contexts;
    }

    private static Context context;
    private static String TAG = "Listview and ScrollView item 截图:";
    public static final String PARTENIMAGEPATH="/sdcard/myImage/[0-9]*.jpg";
    private INoteInfo iNoteInfo=null;
    private DBdataSource dBdataSource=null;
    private NoteInfo noteInfo=null;
    private MessageBox messageBox=null;
    private Intent intent=null;
    private String name;
    private String type;


    /*
     將插入新的圖片保存到文件並返回路徑名
     */
    public String SaveToFile(String FilePath,String name,Bitmap bitmap){
        FileOutputStream FOut = null;
        File file = new File(FilePath);
        // File file=StorageUtils.getOwnCacheDirectory
        file.mkdirs();// 創建資料夾
        String fileName = FilePath+name;
        File pfile = new File(FilePath,name);
        try {
            FOut = new FileOutputStream(pfile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, FOut);// 把數據寫入文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                FOut.flush();
                FOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileName;
    }

    /*
     內容中是否包含圖片
     */
    public boolean isHaveImage(String content){
        Pattern p=Pattern.compile(PARTENIMAGEPATH);
        Matcher m=p.matcher(content);
        if(m.find())
            return true;
        else
            return false;
    }

    /*
     將截圖保存到文件
    */
    public File bitMap2File(Bitmap bitmap) {
        String path = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            path = Environment.getExternalStorageDirectory() + File.separator;// 保存到 SD 根目錄
        }


        //        File f = new File(path, System.currentTimeMillis() + ".jpg");
        File f = new File(path, "share" + ".jpg");
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            bitmap.recycle();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return f;
        }
    }

    /*
    截圖壓縮
     */
    public Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 10, baos);//質量壓縮方法，這裡100表示不壓縮，把壓縮後的數據存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 400) {  //循環判斷如果壓縮後圖片是否大於400kb,大於繼續壓縮（這裡可以設置大些）
            baos.reset();//  重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 這裡壓縮options%，把壓縮後的數據存放到baos中
            options -= 10;// 每次都減少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把壓縮後的數據 baos 存放到 ByteArrayInputStream 中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream數據生成圖片
        return bitmap;
    }

    /*
     獲取ScrollView的截屏圖
     */
    public static Bitmap getScrollViewBitmap(ScrollView scrollView, String picpath) {
        int h = 0;
        Bitmap bitmap=null;
        // 獲取 listView 實際高度
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
        }
        // 從 drable 文件中獲取背景圖片轉換成 Bitmap
       Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.timg);
        // 然後根據 ScrollView 的實際高度拉伸背景圖片
       icon=Bitmap.createScaledBitmap(icon,scrollView.getWidth(),h,true);
        Log.d(TAG, "實際高度:" + h);
        Log.d(TAG, " 高度:" + scrollView.getHeight());
        // 創建對應大小的 bitmap ,此時這個 Bitmap 還未畫上任何團
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        // 將拉伸好的背景圖片覆蓋上去,完成畫布的創建
       canvas.drawBitmap(icon,0,0,null);
       // 將 ScrollView 的內容畫到畫布上
        scrollView.draw(canvas);
        return bitmap;
    }


    // 計算圖片的縮放值
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }


    /*
    獲取圖片的比例
    */
    public BitmapFactory.Options  getBili(int reqWidth, int reqHeight){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return options;
    }

    /*
     進一步修飾圖片大小
     */
    public Bitmap jinyibu(Bitmap bitmap,Context context){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int w_width = w_screen;
        int b_width = bitmap.getWidth();
        int b_height = bitmap.getHeight();
        int w_height = w_width * b_height / b_width;
        bitmap = Bitmap.createScaledBitmap(bitmap, w_width, w_height, false);
        return bitmap;
    }

    /*
    解析內容：用到了正則表達式
    獲取 EditView 的整體效果
    返回 SpannableString 對象
     */
    public SpannableString getSpannedString(String content, Context context){
        SpannableString ss = new SpannableString(content);
        Pattern p=Pattern.compile("/sdcard/myImage/[0-9]*.jpg");
        Matcher m=p.matcher(content);
        while(m.find()){
            Bitmap bm = BitmapFactory.decodeFile(m.group());
            Bitmap rbm=bm;
            ImageSpan span = new ImageSpan(context, rbm);
            ss.setSpan(span, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return ss;
    }

    /*
    將一個筆記的所有信息保存到數據庫
     */
    public  void SaveToDB(final String content, final Context context, NoteInfo noteInfos){
        dBdataSource=new DBdataSource(context);
        this.noteInfo=noteInfos;
        if(noteInfo==null){// 如果是新建的筆記
            messageBox=new MessageBox(context);

            messageBox.showInputDialog("新建筆記", "請輸入名稱", null,
                    new MessageBox.ISimpleInputDialogButtonClick() {
                        @Override
                        public void doSomething(String userInput) {
                            if(StringFunction.isNullOrEmpty(userInput)){// 命名不能空白
                                UiHelper.toastShowMessageShort(context,"名字不能空白,請重新輸入");
                                return;
                            }
                            dBdataSource.open();
                            if(dBdataSource.findByName(userInput)){// 如果這個名字已經存在
                                UiHelper.toastShowMessageShort(context,"名字已經存在");
                                dBdataSource.close();
                                return;
                            }
                            dBdataSource.close();
                            name=userInput;
                            noteInfo=new NoteInfo();
                            String date=new MyDate().getDateAndTime();
                            if(isHaveImage(content))
                            type="圖文";
                            else
                                type="文字";
                            noteInfo.setDate(date);
                            noteInfo.setContent(content);
                            noteInfo.setType(type);
                            noteInfo.setName(name);
                            dBdataSource.open();
                            dBdataSource.insert(noteInfo);
                            UiHelper.toastShowMessageShort(context,"新建成功");
                            dBdataSource.close();
                            iNoteInfo.getANewNote(getNoteInfofromDb());
                            /*
                            保存成功就跳轉回主頁面
                             */
                            intent=new Intent();
                            intent.setClass(context,MainActivity.class);
                            context.startActivity(intent);
                        }
                    },null);

        }else{
            // 更改內容
            name=noteInfo.getName();
            dBdataSource.open();
            if(isHaveImage(content)){
                if(!noteInfo.getType().equals("圖文")){
                    dBdataSource.updateType(noteInfo.getId(),"圖文");
                }
            }else{
                if(!noteInfo.equals("文字")){
                    dBdataSource.updateType(noteInfo.getId(),"文字");
                }



            }
            dBdataSource.updateContent(noteInfo.getId(),content);
            dBdataSource.close();
            iNoteInfo.getANewNote(getNoteInfofromDb());
            UiHelper.toastShowMessageShort(context,"保存成功");

            intent=new Intent();                               //保存修改內容就跳轉回主頁面
            intent.setClass(context,MainActivity.class);
            context.startActivity(intent);




        }

        //  iNoteInfo.getANewNote(getNoteInfofromDb());



    }

    /*
    給筆記重新建立名稱
     */
    public void noteRename(final Context context, NoteInfo noteInfos){
        dBdataSource=new DBdataSource(context);
        this.noteInfo=noteInfos;
        messageBox=new MessageBox(context);

        messageBox.showInputDialog("重命名", "請輸入新名稱", noteInfo.getName(),
                new MessageBox.ISimpleInputDialogButtonClick() {
            @Override
            public void doSomething(String userInput) {
                if(userInput.equals(noteInfo.getName())){
                    UiHelper.toastShowMessageShort(context,"名稱沒有改變");
                    return;
                }
                dBdataSource.open();
                if(dBdataSource.findByName(userInput)){//如果這個名字已經存在
                    UiHelper.toastShowMessageShort(context,"名稱已經存在");
                    dBdataSource.close();
                    return;
                }
                name=userInput;
                dBdataSource.updateName(noteInfo.getId(),name);
                dBdataSource.close();
                iNoteInfo.getANewNote(getNoteInfofromDb());

                intent=new Intent();                       ////保存修改名稱就跳轉回主頁面
                intent.setClass(context,MainActivity.class);
                context.startActivity(intent);



            }
        },null);









    }

    /*
    從數據庫獲得一個全新的NoteInfo
     */
    public NoteInfo getNoteInfofromDb() {
        dBdataSource.open();
      NoteInfo  NewnoteInfo=dBdataSource.getByName(name);
        dBdataSource.close();
        return NewnoteInfo;
    }


    /*
    從數據刪除該筆記並跳轉回到 MainActivity
    */
    public  void deleteNote(final NoteInfo noteInfos, final Context context){
        dBdataSource=new DBdataSource(context);
        messageBox=new MessageBox(context);
        messageBox.showOKOrCancelDialog("是否刪除筆記","提示", new MessageBox.IButtonClick() {
            @Override
            public void doSomething() {
                dBdataSource.open();
                dBdataSource.deleteById(noteInfos.getId());
                dBdataSource.close();
                UiHelper.toastShowMessageShort(context,"刪除成功");
                intent=new Intent();
                intent.setClass(context,MainActivity.class);
                context.startActivity(intent);
            }
        },null);
    }
}

