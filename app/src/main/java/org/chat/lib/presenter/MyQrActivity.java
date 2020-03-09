package org.chat.lib.presenter;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.breadwallet.R;
import com.breadwallet.presenter.activities.HomeActivity;
import com.breadwallet.presenter.activities.util.BRActivity;
import com.breadwallet.tools.animation.ElaphantDialogText;
import com.breadwallet.tools.manager.BRSharedPrefs;
import com.breadwallet.tools.qrcode.QRUtils;
import com.breadwallet.tools.util.StringUtil;

import org.chat.lib.utils.Utils;
import org.elastos.sdk.elephantwallet.contact.internal.ContactInterface;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.node.CarrierPeerNode;

public class MyQrActivity extends BRActivity {

    private ImageView mQrImg;
    private TextView mNicknameTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_qr_layout);

        initView();
        initListener();

        EventBus.getDefault().register(this);
    }

    private void initView() {
        mQrImg = findViewById(R.id.my_qr_img);
        mNicknameTv = findViewById(R.id.my_nickname);

        String nickname = BRSharedPrefs.getNickname(this);
        mNicknameTv.setText(nickname);
//        ContactInterface.UserInfo userInfo = CarrierPeerNode.getInstance(this).getUserInfo();
//        if(userInfo == null) return;
//        String carrierAddr = userInfo.getCurrDevCarrierAddr();
        String carrierAddr = BRSharedPrefs.getCarrierId(this);
        if(!StringUtil.isNullOrEmpty(carrierAddr)) {
            Bitmap bitmap = QRUtils.encodeAsBitmap(carrierAddr, Utils.dp2px(this, 300));
            mQrImg.setImageBitmap(bitmap);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1)
    public void acceptFriend(final CarrierPeerNode.RequestFriendInfo requestFriendInfo) {
        Log.d("xidaokun", "HomeActivity#acceptFriend#\nhumancode:"+ requestFriendInfo.humanCode + "\ncontent:" + requestFriendInfo.content);
        final ElaphantDialogText elaphantDialog = new ElaphantDialogText(this);
        elaphantDialog.setMessageStr("添加好友请求");
        elaphantDialog.setPositiveStr("接受");
        elaphantDialog.setNegativeStr("拒绝");
        elaphantDialog.setPositiveListener(new ElaphantDialogText.OnPositiveClickListener() {
            @Override
            public void onClick() {
                CarrierPeerNode.getInstance(MyQrActivity.this).acceptFriend(requestFriendInfo.humanCode, requestFriendInfo.content);
                EventBus.getDefault().post(requestFriendInfo.humanCode);
                elaphantDialog.dismiss();
            }
        });
        elaphantDialog.setNegativeListener(new ElaphantDialogText.OnNegativeClickListener() {
            @Override
            public void onClick() {
                elaphantDialog.dismiss();
            }
        });
        elaphantDialog.show();
    }


    private void initListener() {
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
