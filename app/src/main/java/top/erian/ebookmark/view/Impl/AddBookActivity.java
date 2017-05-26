package top.erian.ebookmark.view.Impl;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import top.erian.ebookmark.BaseActivity;
import top.erian.ebookmark.R;
import top.erian.ebookmark.model.entity.Book;
import top.erian.ebookmark.presenter.SaveBookPresenter;
import top.erian.ebookmark.presenter.impl.SaveBookPresenterImpl;
import top.erian.ebookmark.view.ISaveDataView;

public class AddBookActivity extends BaseActivity implements ISaveDataView {

    private static final int CAMERA_REQUEST = 5576;
    private static final int GALLERY_REQUEST = 5586;


    private EditText bookName;
    private EditText page;
    private Bitmap cover;
    private Button takeFromCam;
    private Button chooseFromPic;
    private ImageView cover_iv;

    private Uri imageUri;

    // Add mode actionStart
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, AddBookActivity.class);
        context.startActivity(intent);
    }
    // Edit mode actionStart
    public static void actionStart(Context context, Book bookIntent) {
        Intent intent = new Intent(context, AddBookActivity.class);
        intent.putExtra("bookName", bookIntent.getBookName());
        intent.putExtra("page", bookIntent.getPage());
        intent.putExtra("cover", bookIntent.getCover());
        intent.putExtra("isEditMode", true);
        context.startActivity(intent);
    }
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
        cover_iv = (ImageView) findViewById(R.id.cover_add_book);

        // Initialize TextView and ImageView (bookName, page, and cover) if edit mode
        Intent intent = getIntent();
        if (intent.getBooleanExtra("isEditMode", false)) {
            bookName.setText(intent.getStringExtra("bookName"));
            page.setText(String.valueOf(intent.getIntExtra("page",0)));
            cover = intent.getParcelableExtra("cover");
            if (cover != null)
                cover_iv.setImageBitmap(cover);
        }

        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(AddBookActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED)
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (ContextCompat.checkSelfPermission(AddBookActivity.this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED)
            permissionList.add(Manifest.permission.CAMERA);

        if (!permissionList.isEmpty()) {
            // If permission is not granted, set two button disabled
            takeFromCam.setEnabled(false);
            chooseFromPic.setEnabled(false);

            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions, 1);
            //0 means only calling requestPermissions(...)  once in our app
        }
        // OnClickListener on two button
        takeFromCam.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Store the image in memory instead of sdcard
                File outputImage  = new File(getExternalCacheDir(), "output_image.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(AddBookActivity.this,
                            "top.erian.ebookmark.fileprovider", outputImage);
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        });

        // Set listener for chooseFromPic button
        chooseFromPic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUEST);
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (progressDialog != null)
            progressDialog.dismiss();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAMERA_REQUEST:
                if (resultCode == RESULT_OK) {
                    try {
                        cover = BitmapFactory.decodeStream(getContentResolver()
                        .openInputStream(imageUri));
                        cover_iv.setImageBitmap(cover);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Cannot capture picture from camera.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                Log.d("Request", "onActivityResult: " + requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        finish();
                        return;
                    }
                }
                takeFromCam.setEnabled(true);
                chooseFromPic.setEnabled(true);
            } else finish();
        }
    }

    //For attriving data from gallery
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // If document type is Uri, handle it with document id
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // Get the numeric id from docId
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content:" +
                        "//downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // if uri is content type, handle that with normal way
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // if uri is file type, directly get the path of picture
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath (uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // Get the actual path of picture
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            cover = BitmapFactory.decodeFile(imagePath);
            cover_iv.setImageBitmap(cover);
        } else {
            Toast.makeText(this, "Failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    // For toolbar menu
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
                book.setPage(Integer.parseInt(page.getText().toString().trim()));
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
        if (TextUtils.isEmpty(bookNameStr)) sb.append("Book name field need to be filled.\n");
        if (TextUtils.isEmpty(pageStr)) sb.append("Page field need to be filled.\n");
        if (cover == null)
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
