package cyource.manasrawat.overfloat;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class FloatingButtonService extends Service {

    private WindowManager windowManager;
    private ImageView floatingButton;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String packageName = intent.getStringExtra("PACKAGE_NAME");
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        floatingButton = new ImageView(this);
        floatingButton.setImageResource(R.mipmap.openfloat);
        floatingButton.setLayoutParams(new LinearLayout.LayoutParams(5, 5));

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        windowManager.addView(floatingButton, params);

        floatingButton.setOnTouchListener(new View.OnTouchListener() {
            int intX;
            int intY;
            float touchX;
            float touchY;
            float distanceX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        intX = params.x;
                        intY = params.y;
                        touchX = event.getRawX();
                        touchY = event.getRawY();
                        distanceX = event.getX();
                        return true;
                    case MotionEvent.ACTION_UP:
                        float distance = distanceX - event.getX();
                        if (distance == 0) {
                            startActivity(getPackageManager().getLaunchIntentForPackage(packageName));
                        }
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        params.x = intX + (int) (event.getRawX() - touchX);
                        params.y = intY + (int) (event.getRawY() - touchY);
                        windowManager.updateViewLayout(floatingButton, params);
                        return true;

                }
                return false;
            }
        });
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingButton != null) {
            windowManager.removeView(floatingButton);
        }
    }
}

