package top.erian.ebookmark.view.Impl;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import top.erian.ebookmark.BaseActivity;
import top.erian.ebookmark.R;
import top.erian.ebookmark.model.entity.Bookmark;
import top.erian.ebookmark.presenter.BookmarksPresenter;
import top.erian.ebookmark.presenter.impl.BookmarksPresenterImpl;
import top.erian.ebookmark.view.ILoadDataView;

public class BookmarkListFragment extends Fragment implements ILoadDataView<Bookmark> {

    protected BaseActivity mActivity;
    private RecyclerView bookmarkRecyclerView;
    private BookmarksPresenter bookmarksPresenter;
    private String bookName;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (BaseActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark_list, container, false);
        bookmarkRecyclerView = (RecyclerView) view.findViewById(R.id.bookmark_list_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        bookmarkRecyclerView.setLayoutManager(layoutManager);
        BookmarkListAdapter adapter = new BookmarkListAdapter(new ArrayList<Bookmark>());
        bookmarkRecyclerView.setAdapter(adapter);

        //this.update();
        return view;
    }

    /**
     * this function is used for passing book name to bookmark list fragment from activity
     * @param bookName
     */
    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    @Override
    public void startLoading() {
        if (mActivity.progressDialog != null) mActivity.progressDialog.show();
    }

    @Override
    public void loadFailed() {
        Toast.makeText(mActivity,
                "Failed to load bookmarks.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadSuccess(List<Bookmark> list) {
        try {
            BookmarkListAdapter adapter = (BookmarkListAdapter) bookmarkRecyclerView.getAdapter();
            adapter.mBookmarkList.clear();
            adapter.mBookmarkList.addAll(list);
            adapter.notifyDataSetChanged();
        } catch (NullPointerException npe) {
            loadFailed();
            Log.d("Null pointer: ", "loadFailed: null pointer detected");
            npe.printStackTrace();
        } catch (Exception e) {
            loadFailed();
            e.printStackTrace();
        }
    }

    @Override
    public void finishLoading() {
        if (mActivity.progressDialog != null) mActivity.progressDialog.dismiss();
    }

    @Override
    public void update() {
        if (bookmarksPresenter == null) bookmarksPresenter = new BookmarksPresenterImpl(this);
        bookmarksPresenter.getBookmarks(bookName);
    }

    /*** BookmarkListAdapter
     * this is used to show bookmark on fragment
     */
    class BookmarkListAdapter extends RecyclerView.Adapter<BookmarkListAdapter.ViewHolder> {

        private List<Bookmark> mBookmarkList;

        public BookmarkListAdapter(List<Bookmark> bookmarkList) {
            mBookmarkList = bookmarkList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.bookmark_item, parent, false);
            final ViewHolder holder = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bookmark bookmark = mBookmarkList.get(holder.getAdapterPosition());
                    // Start BookmarkDetailActivity and pass the whole bookmark to it.
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Bookmark bookmark = mBookmarkList.get(position);
            holder.currentPage.setText(String.valueOf(bookmark.getCurrentPage()));
            holder.page.setText(String.valueOf(bookmark.getCurrentPage()));
            holder.date.setText(bookmark.getCreateDate().toString());
            holder.note.setText(bookmark.getNote());
        }

        @Override
        public int getItemCount() {
            return mBookmarkList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView currentPage;

            TextView page;

            TextView date;

            TextView note;

            public ViewHolder(View view) {
                super(view);
                currentPage = (TextView) view.findViewById(R.id.bm_current_page);
                page = (TextView) view.findViewById(R.id.bm_page);
                date = (TextView) view.findViewById(R.id.bm_date);
                note = (TextView) view.findViewById(R.id.bm_note);
             }
        }
    }
}
