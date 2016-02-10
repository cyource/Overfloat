package cyource.manasrawat.overfloat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Run extends android.app.Service {

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
    public int onStartCommand(Intent intent, int flags, final int startId) {
        final String packageName = intent.getStringExtra("PACKAGE_NAME");
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        floatingButton = new ImageView(this);
        floatingButton.setX(-100);
        floatingButton.animate().translationX(0);
        floatingButton.setImageResource(R.mipmap.floatingbutton);
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

        floatingButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                stopSelf();
                return true;
            }
        });

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
                        return false;
                    case MotionEvent.ACTION_UP:
                        float distance = distanceX - event.getX();
                        if (distance == 0)
                            startActivity(getPackageManager().getLaunchIntentForPackage(packageName));
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
            floatingButton.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    windowManager.removeView(floatingButton);
                }
            });
        }
    }
}

