package top.erian.ebookmark.view.Impl;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import top.erian.ebookmark.BaseActivity;
import top.erian.ebookmark.R;
import top.erian.ebookmark.model.entity.Book;
import top.erian.ebookmark.presenter.BooksPresenter;
import top.erian.ebookmark.presenter.impl.BooksPresenterImpl;
import top.erian.ebookmark.view.ILoadDataView;


public class BookListFragment extends Fragment implements ILoadDataView<Book>{

    protected BaseActivity mActivity;
    private RecyclerView bookListRecyclerView;
    private BooksPresenter booksPresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (BaseActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);
        bookListRecyclerView = (RecyclerView) view.findViewById(R.id.book_list_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        bookListRecyclerView.setLayoutManager(layoutManager);
        BookListAdapter adapter = new BookListAdapter(new ArrayList<Book>());
        bookListRecyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void startLoading() {
        if (mActivity.progressDialog != null) mActivity.progressDialog.show();
    }

    @Override
    public void loadFailed() {
        Toast.makeText(mActivity, "Failed to load books.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadSuccess(List<Book> list) {
        //Log.d("Book list size", "loadSuccess: " + String.valueOf(list.size()));
        try {
            BookListAdapter adapter = (BookListAdapter) bookListRecyclerView.getAdapter();
            adapter.mBookList.clear();
            adapter.mBookList.addAll(list);
            //Log.d("mBookList size", "loadSuccess: " + String.valueOf(adapter.mBookList.size()));
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
        if (booksPresenter == null) booksPresenter = new BooksPresenterImpl(this);
        booksPresenter.getBooks();
    }

    /*** BookListAdapter
     * this adapter is for the RecyclerView of book list
     */
    class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.ViewHolder> {

        final private List<Book> temp;
        final private List<Book> mBookList;

        class ViewHolder extends RecyclerView.ViewHolder{
            View bookListView;

            ImageView bookCover;

            TextView bookName;

            ProgressBar readProgress;

            TextView currentPage;

            TextView page;

            public ViewHolder(View view) {
                super(view);
                bookListView = view;
                bookCover = (ImageView) view.findViewById(R.id.book_cover);
                bookName = (TextView) view.findViewById(R.id.book_name);
                readProgress = (ProgressBar) view.findViewById(R.id.read_progress);
                currentPage = (TextView) view.findViewById(R.id.current_page);
                page = (TextView) view.findViewById(R.id.page);
            }
        }

        public BookListAdapter(List<Book> mBookList) {
            this.mBookList = mBookList;
            this.temp = new ArrayList<>(mBookList);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.book_item, parent, false);
            final ViewHolder holder = new ViewHolder(view);
            holder.bookListView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    Book book = mBookList.get(position);
                    // Start BookDetailActivity with bookname and cover
                    BookDetailActivity.actionStart(mActivity,
                            book.getBookName(), book.getCover());
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Book book = mBookList.get(position);
            holder.bookName.setText(book.getBookName());
            holder.bookCover.setImageBitmap(book.getCover());
            holder.page.setText(String.valueOf(book.getPage()));
            holder.currentPage.setText(String.valueOf(book.getCurrentPage()));
            holder.readProgress.setMax(book.getPage());
            holder.readProgress.setProgress(book.getCurrentPage());
        }

        @Override
        public int getItemCount() {
            return mBookList.size();
        }

        public boolean areItemsTheSame(Book oldItem, Book newItem) {
            return oldItem.equals(newItem);
        }
        public boolean areContentsTheSame(Book oldItem, Book newItem) {
            if (oldItem.sameCover(newItem) == false) return false;
            if (oldItem.samePage(newItem) == false) return false;
            //Log.d("BookListAdapter", "areContentsTheSame: " + oldItem.sameCurrentPage(newItem));
            Log.d("oldItem", "areContentsTheSame: " + String.valueOf(oldItem));
            Log.d("newItem", "areContentsTheSame: " + String.valueOf(newItem));
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
                    return mBookList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return BookListAdapter.this.areItemsTheSame(temp.get(oldItemPosition),
                            mBookList.get(newItemPosition));
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return BookListAdapter.this.areContentsTheSame(temp.get(oldItemPosition),
                            mBookList.get(newItemPosition));
                }
            });
            diffResult.dispatchUpdatesTo(this);
            // Update duplicate list to the latest
            temp.clear();
            temp.addAll(mBookList);
        }
    }
}
