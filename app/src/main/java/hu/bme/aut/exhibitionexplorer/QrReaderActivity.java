package hu.bme.aut.exhibitionexplorer;

import android.content.Intent;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import hu.bme.aut.exhibitionexplorer.data.Artifact;

public class QrReaderActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener{

    private QRCodeReaderView qrCodeReaderView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_reader);

        qrCodeReaderView = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setOnQRCodeReadListener(this);

        qrCodeReaderView.setBackCamera();
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        Intent data = new Intent();
        data.putExtra(Artifact.KEY_ARTIFACT_ID, text);
        setResult(RESULT_OK, data);
        finish();
    }
}
