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
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import top.erian.ebookmark.R;
import top.erian.ebookmark.model.entity.Book;
import top.erian.ebookmark.presenter.BooksPresenter;
import top.erian.ebookmark.presenter.impl.BooksPresenterImpl;
import top.erian.ebookmark.util.BookListAdapter;
import top.erian.ebookmark.view.ILoadDataView;

public class MainActivity extends AppCompatActivity implements ILoadDataView<Book>{

    private List<Book> bookList = null;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));

        BooksPresenter booksPresenter = new BooksPresenterImpl(this);
        booksPresenter.getBooks();
    }

    @Override
    public void startLoading() {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Read data from database");
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void loadFailed() {
        Toast.makeText(MainActivity.this, "Failed to load books.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadSuccess(List<Book> list) {
        bookList = list;
        if (bookList == null) return;

        ListView listView = (ListView) findViewById(R.id.book_list_view);
        BookListAdapter adapter = new BookListAdapter(MainActivity.this, R.layout.book_item,
                bookList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Book book = bookList.get(position);
                // Go to next Activity: BookmarkActivity
                Intent intent = new Intent(MainActivity.this, BookDetailActivity.class);
                intent.putExtra("bookName", book.getBookName());
                startActivityForResult(intent,1);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                // Pop up a dialog which let user choose what to do with the specific book
                return true;
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
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_book:
                // Go to search book activity
                Toast.makeText(MainActivity.this, "Search!", Toast.LENGTH_SHORT).show();
                break;
           case R.id.add_book:
                // Go to add book activity
                Toast.makeText(MainActivity.this, "Add!", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
    }
}
