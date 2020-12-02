package vexetal.box.vexetalbox.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import vexetal.box.vexetalbox.R;

public class Permission extends AppCompatActivity {


    private Button btnGrant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        if(ContextCompat.checkSelfPermission(Permission.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                &&ContextCompat.checkSelfPermission(Permission.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            startActivity(new Intent(Permission.this, Login.class));
            finish();
            return;
        }

        btnGrant = findViewById(R.id.btn_grant);

        btnGrant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(Permission.this)
                        .withPermissions(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {

                                if(report.areAllPermissionsGranted()) {
                                    startActivity(new Intent(Permission.this, Login.class));
                                    finish();
                                }
                                else{
                                    final SweetAlertDialog sDialog = new SweetAlertDialog(Permission.this, SweetAlertDialog.SUCCESS_TYPE);
                                    sDialog.setTitleText("Warning!");
                                    sDialog.setContentText("Some permissions have been denied permanently. Please go to setting to enable!");
                                    sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismiss();
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                                        }
                                    });
                                    sDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sDialog.dismiss();
                                        }
                                    });
                                    sDialog.setCancelable(false);
                                    sDialog.show();


                                }

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                            }
                        })
                        .check();
            }
        });
    }

}
