package top.erian.ebookmark.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import top.erian.ebookmark.model.entity.Bookmark;

/**
 * Created by root on 17-5-3.
 */

public class BookmarkListAdapter extends ArrayAdapter<Bookmark> {

    private int resourceId;
    private int page;

    public BookmarkListAdapter(Context context, int resource, List<Bookmark> objects) {
        super(context, resource, objects);
        resourceId = resource;
        this.page = 0;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Bookmark bookmark = getItem(position);
        View view;
        ViewHolder viewHolder;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent,
                    false);
            viewHolder = new ViewHolder();
            viewHolder.currentPage.setText(String.valueOf(bookmark.getCurrentPage()));
            viewHolder.page.setText(String.valueOf(page));
            viewHolder.note.setText(bookmark.getNote().substring(0, 15) + "..."); // Partly display
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        return view;
    }

    class ViewHolder {
        TextView currentPage;

        TextView page;

        TextView date;

        TextView note;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
