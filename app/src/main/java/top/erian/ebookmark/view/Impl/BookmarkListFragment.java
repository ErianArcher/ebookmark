package top.erian.ebookmark.view.Impl;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import top.erian.ebookmark.BaseActivity;
import top.erian.ebookmark.R;
import top.erian.ebookmark.model.entity.Book;
import top.erian.ebookmark.model.entity.Bookmark;
import top.erian.ebookmark.presenter.BookmarksPresenter;
import top.erian.ebookmark.presenter.DeleteBookmarkPresenter;
import top.erian.ebookmark.presenter.impl.BookmarksPresenterImpl;
import top.erian.ebookmark.presenter.impl.DeleteBookmarkPresenterImpl;
import top.erian.ebookmark.util.ContextMenuRecyclerView;
import top.erian.ebookmark.view.IDeleteDataView;
import top.erian.ebookmark.view.ILoadDataView;

public class BookmarkListFragment extends Fragment implements ILoadDataView<Bookmark> {

    protected BaseActivity mActivity;
    private ContextMenuRecyclerView bookmarkRecyclerView;
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
        bookmarkRecyclerView = (ContextMenuRecyclerView) view.findViewById(R.id.bookmark_list_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        bookmarkRecyclerView.setLayoutManager(layoutManager);
        BookmarkListAdapter adapter = new BookmarkListAdapter(new ArrayList<Bookmark>());
        bookmarkRecyclerView.setAdapter(adapter);

        bookmarkRecyclerView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                MenuInflater menuInflater = mActivity.getMenuInflater();
                menuInflater.inflate(R.menu.context_menu, menu);
            }
        });
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
                "Failed to load bookmarks. There may be no bookmark.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadSuccess(List<Bookmark> list) {
        try {
            BookmarkListAdapter adapter = (BookmarkListAdapter) bookmarkRecyclerView.getAdapter();
            adapter.mBookmarkList.clear();
            adapter.mBookmarkList.addAll(list);
            adapter.notifyDiff();
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

    //Context menu
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ContextMenuRecyclerView.RecyclerContextMenuInfo info =
                (ContextMenuRecyclerView.RecyclerContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case (R.id.context_edit):
                // Go to the edit activity of book
                editBookmark(info.position);
                break;
            case (R.id.context_delete):
                // Delete the book
                deleteBookmark(info.position);
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }
    private void editBookmark(int position) {
        // Go to editBookmark activity
        final Bookmark bookmark = ((BookmarkListAdapter)bookmarkRecyclerView.getAdapter())
                .mBookmarkList.get(position);
        AddBookmarkActivity.actionStart(mActivity, bookName, bookmark);

    }
    private void deleteBookmark(int position) {
        final Bookmark bookmark = ((BookmarkListAdapter)bookmarkRecyclerView.getAdapter())
                .mBookmarkList.get(position);
        // Delete book here
        AlertDialog.Builder dialog = new AlertDialog.Builder (mActivity);
        dialog.setTitle("Delete operation")
                .setMessage("This bookmark on current page of" + bookmark.getCurrentPage() +
                        " will be deleted.")
                .setCancelable(true)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteBookmarkPresenter deleteBookmarkPresenter =
                                new DeleteBookmarkPresenterImpl(new IDeleteDataView() {
                                    @Override
                                    public void startDeleting() {

                                    }

                                    @Override
                                    public void deleteFailed() {
                                        Log.d("Bookmark " + bookmark.getCurrentPage(),
                                                "deleteFailed: "+bookName);
                                    }

                                    @Override
                                    public void deleteSuccess() {
                                        Log.d("Bookmark " + bookmark.getCurrentPage(),
                                                "deleteSuccess: "+bookName);
                                    }

                                    @Override
                                    public void deleteFinished() {
                                        update();//update the recyclerview
                                    }
                                });
                        deleteBookmarkPresenter.deleteBookmark(bookName, bookmark);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

    }

    /*** BookmarkListAdapter
     * this is used to show bookmark on fragment
     */
    class BookmarkListAdapter extends RecyclerView.Adapter<BookmarkListAdapter.ViewHolder> {

        private List<Bookmark> temp;
        private List<Bookmark> mBookmarkList;

        public BookmarkListAdapter(List<Bookmark> bookmarkList) {
            mBookmarkList = bookmarkList;
            temp = new ArrayList<>(mBookmarkList);
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
            holder.itemView.setLongClickable(true);
        }

        @Override
        public int getItemCount() {
            return mBookmarkList.size();
        }

        public boolean areItemsTheSame(Bookmark oldItem, Bookmark newItem) {
            return oldItem.equals(newItem);
        }
        public boolean areContentsTheSame(Bookmark oldItem, Bookmark newItem) {
            if (oldItem.sameNote(newItem) == false) return false;
            return oldItem.sameCurrentPage(newItem);
        }

        public void notifyDiff() {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return temp.size();
                }

                @Override
                public int getNewListSize() {
                    return mBookmarkList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return BookmarkListAdapter.this.areItemsTheSame(temp.get(oldItemPosition),
                            mBookmarkList.get(newItemPosition));
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return BookmarkListAdapter.this.areContentsTheSame(temp.get(oldItemPosition),
                            mBookmarkList.get(newItemPosition));
                }
            });
            diffResult.dispatchUpdatesTo(this);
            // Update duplicate list to the latest
            temp.clear();
            temp.addAll(mBookmarkList);
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
