package com.example.lenovo.Android_LIFE_Journal.function;

import android.content.Context;
import android.os.AsyncTask;

import com.example.lenovo.Android_LIFE_Journal.model.NoteInfo;
import com.example.lenovo.Android_LIFE_Journal.repository.Myrepository;

import java.util.List;

public class SearchNoteAsyncTask extends AsyncTask<Void ,Void,Void > {

    private List<NoteInfo> noteInfoList=null;
    private IExplorerUIComponent uiComponent=null;
    private Context context;


    /*
    MainActivity 必須實現的接口,用來刷新 ListView
     */
    public interface IExplorerUIComponent{
        /*
        顯示記事簿的信息
         */
        void showNoteInfos(List<NoteInfo> Info);
    }

    /*
    構造方法，傳遞一個上下文和一個 this
     */
    public SearchNoteAsyncTask(IExplorerUIComponent uiComponent, Context context){
        this.uiComponent=uiComponent;
        this.context=context;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        noteInfoList=new Myrepository(context).getNoteInfo();

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        if(uiComponent!=null){
            uiComponent.showNoteInfos(noteInfoList);
        }
        super.onPostExecute(aVoid);
    }
}
