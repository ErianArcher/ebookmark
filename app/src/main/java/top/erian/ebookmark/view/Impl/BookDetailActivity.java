package top.erian.ebookmark.view.Impl;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import top.erian.ebookmark.BaseActivity;
import top.erian.ebookmark.R;
import top.erian.ebookmark.model.entity.Book;
import top.erian.ebookmark.presenter.BooksPresenter;
import top.erian.ebookmark.presenter.impl.BooksPresenterImpl;
import top.erian.ebookmark.view.ILoadDataView;

public class BookDetailActivity extends BaseActivity{

    private String bookName;
    private Bitmap cover;
    private ImageView coverIV;
    private BookmarkListFragment bookmarkListFragment;

    public static void actionStart(Context context, String bookName, Bitmap cover) {
        Intent intent = new Intent(context, BookDetailActivity.class);
        intent.putExtra("bookName", bookName);
        intent.putExtra("cover", cover);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        // Set the toolbar
        Intent intent = getIntent();
        bookName = intent.getStringExtra("bookName");
        cover = (Bitmap) intent.getParcelableExtra("cover");

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        if (bookName != null) {
            toolbar.setTitle(bookName);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Display back button

        // Set a bitmap for ImageView
        coverIV = (ImageView) findViewById(R.id.book_cover_detail);
        if (coverIV == null) Log.d("CoverIV", "onCreate: null");
        if (cover == null) Log.d("cover", "onCreate: null");
        if (cover != null) coverIV.setImageBitmap(cover);

        // Initial the progressDialog
        progressDialog = new ProgressDialog(BookDetailActivity.this);
        progressDialog.setTitle("Read data from database");
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        //Initialize bookmark list fragment
        bookmarkListFragment = (BookmarkListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.bookmark_list_fragment);
        bookmarkListFragment.setBookName(bookName);
        bookmarkListFragment.update();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bookmarkListFragment.update();
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
                AddBookmarkActivity.actionStart(BookDetailActivity.this, bookName);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Log.d("BookDetailActivity", "Bookmark list of " +
                            this.bookName + " need to be updated.");
                }
                break;
            default:
        }
    }
}
