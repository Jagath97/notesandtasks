package com.example.jagath.notesandtasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.androidadvance.topsnackbar.TSnackbar;

import java.util.Collections;
import java.util.List;

/**
 * Created by jagath on 17/03/2018.
 */

public class NotesRecyclerAdapter extends RecyclerView.Adapter<NotesRecyclerAdapter.RecyclerViewHolder> {
    //private Activity activity;
    private List<NoteContent> list= Collections.emptyList();
    private Context context;
    private DataBaseHelper helper;
    private View view;
    private int lastPosition=-1;
    private OnTapListener onTapListener;
    private GetNoteFromDb getNoteFromDb;
    private int flag;

    public NotesRecyclerAdapter( List<NoteContent> list,Context context,int flag,View view) {
        this.list = list;
        this.context=context;
        this.flag=flag;
        this.view=view;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_layout,parent,false);
        return new RecyclerViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
        holder.title.setText(list.get(position).getTitle());
        holder.content.setText(list.get(position).getContent());
        holder.cardView.setCardBackgroundColor(list.get(position).getColor());
        setAnimation(holder.itemView, position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTapListener != null) {
                    //onTapListener.OnTapView(list.get(position).getId());
                    int id=list.get(position).getId();
                    String content=list.get(position).getContent();
                    String title=list.get(position).getTitle();
                    String edited=list.get(position).getModified();
                    int color=list.get(position).getColor();
                    Intent intent=new Intent(context,NoteCreation.class);
                    intent.putExtra("id",id);
                    intent.putExtra("color",color);
                    intent.putExtra("content",content);
                    intent.putExtra("title",title);
                    intent.putExtra("modified",edited);
                    context.startActivity(intent);

                }
            }
        });
        if (flag == 0) {
            holder.option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupMenu popupMenu = new PopupMenu(context, holder.option);
                    popupMenu.inflate(R.menu.card_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_archive:
                                    final NoteContent e=list.get(position);
                                    archive(position, context);
                                    list.remove(position);
                                    notifyDataSetChanged();
                                    TSnackbar snackbar2=TSnackbar.make(view,"Archived",Snackbar.LENGTH_SHORT)
                                            .setActionTextColor(Color.parseColor("#CC00CC")).setAction("UNDO", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            list.add(e);
                                            unarchive(list.size()-1,context);
                                            notifyDataSetChanged();
                                        }
                                    });
                                    View snackbarView = snackbar2.getView();
                                    snackbarView.setBackgroundColor(Color.parseColor("#323232"));
                                    TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
                                    textView.setTextColor(Color.WHITE);
                                    snackbar2.show();
                                    break;
                                case R.id.menu_delete:
                                    final NoteContent d=list.get(position);
                                    dismmiss(position,context);
                                    list.remove(position);
                                    notifyDataSetChanged();
                                    TSnackbar snackbar3=TSnackbar.make(view,"Deleted",Snackbar.LENGTH_SHORT).setActionTextColor(Color.parseColor("#CC00CC")).setAction("UNDO", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            list.add(d);
                                            onRecreateNote(list.size()-1,context);
                                            notifyDataSetChanged();
                                        }
                                    });
                                    View snackbarView1 = snackbar3.getView();
                                    snackbarView1.setBackgroundColor(Color.parseColor("#323232"));
                                    TextView textView1 = (TextView) snackbarView1.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
                                    textView1.setTextColor(Color.WHITE);
                                    snackbar3.show();
                                    break;

                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });
        }
        if(flag==1){
            holder.option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final PopupMenu popupMenu = new PopupMenu(context, holder.option);
                    popupMenu.inflate(R.menu.arch_card_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.arch_restore:
                                    final NoteContent e=list.get(position);
                                    unarchive(position, context);
                                    list.remove(position);
                                    notifyDataSetChanged();
                                    Snackbar snackbar=Snackbar.make(view,"UnArchived",Snackbar.LENGTH_SHORT).setAction("UNDO", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            list.add(e);
                                            archive(list.size()-1,context);
                                            notifyDataSetChanged();
                                        }
                                    });
                                    snackbar.show();
                                    break;
                                case R.id.arch_delete:
                                    final NoteContent d=list.get(position);
                                    dismmiss(position,context);
                                    list.remove(position);
                                    notifyDataSetChanged();
                                    Snackbar snackbar1=Snackbar.make(view,"Deleted",Snackbar.LENGTH_SHORT).setAction("UNDO", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            list.add(d);
                                            onRecreateNote(list.size()-1,context);
                                            notifyDataSetChanged();
                                        }
                                    });
                                    snackbar1.show();
                                    break;

                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void onRecreateNote(int position,Context context){
        helper=new DataBaseHelper(context);
        int id=list.get(position).getId();
        String title=list.get(position).getTitle();
        String content=list.get(position).getContent();
        String modifed=list.get(position).getModified();
        String status=list.get(position).getStatus();
        String pinned=list.get(position).getPin();
        int color=list.get(position).getColor();
        boolean res=helper.createNewNote(title,content,modifed,pinned,color,status);
        String s= String.valueOf(res);
        //Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
    }
    public void  setOnTapListener(OnTapListener onTapListener){
        this.onTapListener=onTapListener;
    }
    public void setGetNoteFromDb(GetNoteFromDb getNoteFromDb){this.getNoteFromDb=getNoteFromDb;}
    public void dismmiss(int position,Context context){
        helper=new DataBaseHelper(context);
        int id=list.get(position).getId();
        //Toast.makeText(context,"Deleted",Toast.LENGTH_LONG).show();
        helper.deleteNote(id);
    }
    public void archive(int position,Context context){
        helper=new DataBaseHelper(context);
        int id=list.get(position).getId();
        helper.archiveNote(id);
        //Toast.makeText(context,"Archived",Toast.LENGTH_LONG).show();

    }
    public void unarchive(int position,Context context){
        helper=new DataBaseHelper(context);
        int id=list.get(position).getId();
        helper.unarchiveNote(id);
        //Toast.makeText(context,"UnArchived",Toast.LENGTH_LONG).show();
    }
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
    public TextView title,content,option;
    public CardView cardView;
    public RecyclerViewHolder(View itemView) {
        super(itemView);
        option=itemView.findViewById(R.id.optiondigit);
        title=itemView.findViewById(R.id.title_note);
        content=itemView.findViewById(R.id.content_note);
        cardView=itemView.findViewById(R.id.card_view);
    }

}
}


