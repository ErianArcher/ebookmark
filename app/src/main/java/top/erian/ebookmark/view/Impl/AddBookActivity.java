package top.erian.ebookmark.view.Impl;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import top.erian.ebookmark.R;
import top.erian.ebookmark.model.entity.Book;
import top.erian.ebookmark.presenter.SaveBookPresenter;
import top.erian.ebookmark.presenter.impl.SaveBookPresenterImpl;
import top.erian.ebookmark.view.ISaveDataView;

public class AddBookActivity extends AppCompatActivity implements ISaveDataView {

    private ProgressDialog progressDialog;
    private EditText bookName;
    private EditText page;
    private Bitmap cover;
    private Button takeFromCam;
    private Button chooseFromPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        Toolbar toolbar = (Toolbar) findViewById(R.id.add_book_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        bookName = (EditText) findViewById(R.id.book_name_add_book);
        page = (EditText) findViewById(R.id.page_add_book);
        cover = null;
        takeFromCam = (Button) findViewById(R.id.take_from_cam_add_book);
        chooseFromPic = (Button) findViewById(R.id.choose_from_pic_add_book);
    }

    @Override
    public void startSaving() {
        progressDialog = new ProgressDialog(AddBookActivity.this);
        progressDialog.setTitle("Saving book");
        progressDialog.setMessage("On saving...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void saveFailed() {
        Toast.makeText(AddBookActivity.this, "Fail to save...",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void saveSuccess() {
        Toast.makeText(AddBookActivity.this, "Save successfully...",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void saveFinished() {
        progressDialog.dismiss();
        progressDialog = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.tick:
               //TODO: save the book info
                if (checkInput() == false) break;// Do nothing if information is incomplete
                Book book = new Book();
                book.setBookName(bookName.getText().toString().trim());
                book.setPage(Integer.valueOf(page.getText().toString().trim()));
                book.setCover(cover);
                SaveBookPresenter saveBookPresenter = new SaveBookPresenterImpl(this);
                saveBookPresenter.saveBooks(book);
                setResult(RESULT_OK);
                finish();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkInput() {
        String bookNameStr = bookName.getText().toString().trim();
        String pageStr = page.getText().toString().trim();
        StringBuffer sb = new StringBuffer();
        if (bookNameStr.isEmpty()) sb.append("Book name field need to be filled.\n");
        if (pageStr.isEmpty()) sb.append("Page field need to be filled.\n");
        if (cover == null) // Skip, use default
            cover = BitmapFactory.decodeResource(getResources(), R.drawable.ic_tick);
        if (sb.length() > 0) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(AddBookActivity.this)
                    .setTitle("Error")
                    .setMessage(sb.toString())
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            dialog.show();
            sb = null;
            return false;
        }
        return true;
    }
}
