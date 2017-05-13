package top.erian.ebookmark.view.Impl;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import top.erian.ebookmark.BaseActivity;
import top.erian.ebookmark.R;
import top.erian.ebookmark.model.entity.Book;
import top.erian.ebookmark.presenter.BooksPresenter;
import top.erian.ebookmark.presenter.impl.BooksPresenterImpl;
import top.erian.ebookmark.view.ILoadDataView;

public class MainActivity extends BaseActivity{

    BookListFragment bookListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));

        // Initial the progressDialog
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Read data from database");
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        bookListFragment = (BookListFragment)
                getSupportFragmentManager().findFragmentById(R.id.book_list_fragment);
        bookListFragment.update();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainActivity", "onResume: update");
        bookListFragment.update();
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
                Intent intent = new Intent(MainActivity.this, AddBookActivity.class);
                startActivityForResult(intent, 1);
                break;
            default:
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Log.d("MainActivity", "Book list need to be updated.");
                }
                break;
            default:
        }
    }
}
