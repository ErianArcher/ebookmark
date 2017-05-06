package top.erian.ebookmark.view.Impl;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import top.erian.ebookmark.R;
import top.erian.ebookmark.model.entity.Book;
import top.erian.ebookmark.model.entity.Bookmark;
import top.erian.ebookmark.presenter.BooksPresenter;
import top.erian.ebookmark.presenter.impl.BooksPresenterImpl;
import top.erian.ebookmark.util.BookmarkListAdapter;
import top.erian.ebookmark.view.ILoadDataView;

public class BookDetailActivity extends AppCompatActivity implements ILoadDataView<Book>{

    private String bookName = null;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        // Set the toolbar
        Intent intent = getIntent();
        bookName = intent.getStringExtra("bookName");
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        if (bookName != null) {
            toolbar.setTitle(bookName);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Display back button

        BooksPresenter booksPresenter = new BooksPresenterImpl(this);
        booksPresenter.getBook(bookName);
    }

    @Override
    public void startLoading() {
        progressDialog = new ProgressDialog(BookDetailActivity.this);
        progressDialog.setTitle("The details of " + bookName + " will be presented");
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void loadFailed() {
        Toast.makeText(BookDetailActivity.this,
                "Failed to load the details of " + bookName + ".",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadSuccess(List<Book> list) {
        Book book = list.get(0);
        if (book == null) {
            loadFailed();
            return;
        }

        ImageView iv = (ImageView) findViewById(R.id.book_cover_detail);
        iv.setImageBitmap(book.getCover());

        ListView bookmarks = (ListView) findViewById(R.id.bookmarks_list_view);
        BookmarkListAdapter bookmarkListAdapter = new BookmarkListAdapter(BookDetailActivity.this,
                R.layout.bookmark_item, Arrays.asList(book.getBookmarks()));
        bookmarkListAdapter.setPage(book.getPage());
        bookmarks.setAdapter(bookmarkListAdapter);
        bookmarks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Go to bookmark_detail activity
            }
        });
    }

    @Override
    public void finishLoading() {
        progressDialog.dismiss();
        progressDialog = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.add_bookmark:
                Intent intent = new Intent(BookDetailActivity.this, AddBookmarkActivity.class);
                startActivity(intent);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}
