package top.erian.ebookmark.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import top.erian.ebookmark.R;
import top.erian.ebookmark.model.entity.Book;

/**
 * Created by root on 17-5-2.
 */

public class BookListAdapter extends ArrayAdapter<Book> {

    private int resourceId;

    public BookListAdapter(Context context, int resource, List<Book> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Book book = getItem(position);
        View view;
        ViewHolder viewHolder;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent,
                    false);
            viewHolder = new ViewHolder();
            viewHolder.bookCover = (ImageView) view.findViewById(R.id.book_cover);
            viewHolder.bookName = (TextView) view.findViewById(R.id.book_name);
            viewHolder.readProgress = (ProgressBar) view.findViewById(R.id.read_progress);
            viewHolder.currentPage = (TextView) view.findViewById(R.id.current_page);
            viewHolder.page = (TextView) view.findViewById(R.id.page);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.bookCover.setImageBitmap(book.getCover());
        viewHolder.bookName.setText(book.getBookName());
        viewHolder.readProgress.setProgress(book.getCurrentPage());
        viewHolder.readProgress.setMax(book.getPage());
        viewHolder.currentPage.setText(String.valueOf(book.getCurrentPage()));
        viewHolder.page.setText(String.valueOf(book.getPage()));
        return view;
    }

    class ViewHolder {
        ImageView bookCover;

        TextView bookName;

        ProgressBar readProgress;

        TextView currentPage;

        TextView page;
    }
}
