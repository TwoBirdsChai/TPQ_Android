/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zcmedical.tangpangquan.activity;

import hirondelle.date4j.DateTime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.NormalFileMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VideoMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;
import com.easemob.util.VoiceRecorder;
import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.component.BasicDialog;
import com.zcmedical.common.utils.DialogUtils;
import com.zcmedical.common.utils.ImageUtil;
import com.zcmedical.huanxin.CommonUtils;
import com.zcmedical.huanxin.DemoHXSDKHelper;
import com.zcmedical.huanxin.ExpandGridView;
import com.zcmedical.huanxin.ExpressionAdapter;
import com.zcmedical.huanxin.ExpressionPagerAdapter;
import com.zcmedical.huanxin.HXSDKHelper;
import com.zcmedical.huanxin.MessageAdapter;
import com.zcmedical.huanxin.PasteEditText;
import com.zcmedical.huanxin.SmileUtils;
import com.zcmedical.tangpangquan.R;

/**
 * 聊天页面
 * 
 */
public class ChatActivity extends BaseActivity implements OnClickListener, EMEventListener {

    private static final int REQUEST_CODE_EMPTY_HISTORY = 2;
    public static final int REQUEST_CODE_CONTEXT_MENU = 3;
    private static final int REQUEST_CODE_MAP = 4;
    public static final int REQUEST_CODE_TEXT = 5;
    public static final int REQUEST_CODE_VOICE = 6;
    public static final int REQUEST_CODE_PICTURE = 7;
    public static final int REQUEST_CODE_LOCATION = 8;
    public static final int REQUEST_CODE_NET_DISK = 9;
    public static final int REQUEST_CODE_FILE = 10;
    public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
    public static final int REQUEST_CODE_PICK_VIDEO = 12;
    public static final int REQUEST_CODE_DOWNLOAD_VIDEO = 13;
    public static final int REQUEST_CODE_VIDEO = 14;
    public static final int REQUEST_CODE_DOWNLOAD_VOICE = 15;
    public static final int REQUEST_CODE_SELECT_USER_CARD = 16;
    public static final int REQUEST_CODE_SEND_USER_CARD = 17;
    public static final int REQUEST_CODE_CAMERA = 18;
    public static final int REQUEST_CODE_LOCAL = 19;
    public static final int REQUEST_CODE_CLICK_DESTORY_IMG = 20;
    public static final int REQUEST_CODE_GROUP_DETAIL = 21;
    public static final int REQUEST_CODE_SELECT_VIDEO = 23;
    public static final int REQUEST_CODE_SELECT_FILE = 24;
    public static final int REQUEST_CODE_ADD_TO_BLACKLIST = 25;

    public static final int RESULT_CODE_COPY = 1;
    public static final int RESULT_CODE_DELETE = 2;
    public static final int RESULT_CODE_FORWARD = 3;
    public static final int RESULT_CODE_OPEN = 4;
    public static final int RESULT_CODE_DWONLOAD = 5;
    public static final int RESULT_CODE_TO_CLOUD = 6;
    public static final int RESULT_CODE_EXIT_GROUP = 7;

    public static final int CHATTYPE_SINGLE = 1;
    public static final int CHATTYPE_GROUP = 2;

    public static final String COPY_IMAGE = "EASEMOBIMG";
    private View recordingContainer;
    private ImageView micImage;
    private TextView recordingHint;
    private ListView listView;
    private PasteEditText mEditTextContent;
    private View buttonSetModeKeyboard;
    private View buttonSetModeVoice;
    private View buttonSend;
    private View buttonPressToSpeak;
    // private ViewPager expressionViewpager;
    private LinearLayout emojiIconContainer;
    private LinearLayout btnContainer;
    private ImageView locationImgview;
    private View more;
    private int position;
    private ClipboardManager clipboard;
    private ViewPager expressionViewpager;
    private InputMethodManager manager;
    private List<String> reslist;
    private Drawable[] micImages;
    private int chatType;
    private EMConversation conversation;
    public static ChatActivity activityInstance = null;
    // 给谁发送消息
    private String toChatUsername;
    private VoiceRecorder voiceRecorder;
    private MessageAdapter adapter;
    private File cameraFile;
    static int resendPos;

    //private GroupListener groupListener;

    private ImageView iv_emoticons_normal;
    private ImageView iv_emoticons_checked;
    private RelativeLayout edittext_layout;
    private ProgressBar loadmorePB;
    private boolean isloading;
    private final int pagesize = 20;
    private boolean haveMoreData = true;
    private Button btnMore;
    public String playMsgId;

    private TextView countDownTime;
    private static final long LIMIE_TIME = 172800;
    private LinearLayout rl_bottom;
    private Button btnAsk;

    private Handler micImageHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            // 切换msg切换图片
            micImage.setImageDrawable(micImages[msg.what]);
        }
    };
    private EMGroup group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        activityInstance = this;
        initView();
        setUpView();
    }

    /**
     * initView
     */
    protected void initView() {
        rl_bottom = findView(R.id.rl_bottom);
        btnAsk = findView(R.id.btnAsk);
        btnAsk.setOnClickListener(this);
        countDownTime = findView(R.id.countDownTime);
        recordingContainer = findViewById(R.id.recording_container);
        micImage = (ImageView) findViewById(R.id.mic_image);
        recordingHint = (TextView) findViewById(R.id.recording_hint);
        listView = (ListView) findViewById(R.id.list);
        mEditTextContent = (PasteEditText) findViewById(R.id.et_sendmessage);
        buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard);
        edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
        buttonSetModeVoice = findViewById(R.id.btn_set_mode_voice);
        buttonSend = findViewById(R.id.btn_send);
        buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
        expressionViewpager = (ViewPager) findViewById(R.id.vPager);
        emojiIconContainer = (LinearLayout) findViewById(R.id.ll_face_container);
        btnContainer = (LinearLayout) findViewById(R.id.ll_btn_container);
        locationImgview = (ImageView) findViewById(R.id.btn_location);
        iv_emoticons_normal = (ImageView) findViewById(R.id.iv_emoticons_normal);
        iv_emoticons_checked = (ImageView) findViewById(R.id.iv_emoticons_checked);
        loadmorePB = (ProgressBar) findViewById(R.id.pb_load_more);
        btnMore = (Button) findViewById(R.id.btn_more);
        iv_emoticons_normal.setVisibility(View.VISIBLE);
        iv_emoticons_checked.setVisibility(View.INVISIBLE);
        more = findViewById(R.id.more);
        edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);

        // 动画资源文件,用于录制语音时
        micImages = new Drawable[] { getResources().getDrawable(R.drawable.record_animate_01), getResources().getDrawable(R.drawable.record_animate_02),
                getResources().getDrawable(R.drawable.record_animate_03), getResources().getDrawable(R.drawable.record_animate_04), getResources().getDrawable(R.drawable.record_animate_05),
                getResources().getDrawable(R.drawable.record_animate_06), getResources().getDrawable(R.drawable.record_animate_07), getResources().getDrawable(R.drawable.record_animate_08),
                getResources().getDrawable(R.drawable.record_animate_09), getResources().getDrawable(R.drawable.record_animate_10), getResources().getDrawable(R.drawable.record_animate_11),
                getResources().getDrawable(R.drawable.record_animate_12), getResources().getDrawable(R.drawable.record_animate_13), getResources().getDrawable(R.drawable.record_animate_14), };

        // 表情list
        reslist = getExpressionRes(35);
        // 初始化表情viewpager
        List<View> views = new ArrayList<View>();
        View gv1 = getGridChildView(1);
        View gv2 = getGridChildView(2);
        views.add(gv1);
        views.add(gv2);
        expressionViewpager.setAdapter(new ExpressionPagerAdapter(views));
        edittext_layout.requestFocus();
        voiceRecorder = new VoiceRecorder(micImageHandler);
        //buttonPressToSpeak.setOnTouchListener(new PressToSpeakListen());
        mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
                } else {
                    edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);
                }

            }
        });
        mEditTextContent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
                more.setVisibility(View.GONE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.INVISIBLE);
                emojiIconContainer.setVisibility(View.GONE);
                btnContainer.setVisibility(View.GONE);
            }
        });
        // 监听文字框
        mEditTextContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    btnMore.setVisibility(View.GONE);
                    buttonSend.setVisibility(View.VISIBLE);
                } else {
                    btnMore.setVisibility(View.VISIBLE);
                    buttonSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
    
    
    public void setTitleName(String name){
        ((TextView) findViewById(R.id.name)).setText(name);
    }

    private void setUpView() {
        iv_emoticons_normal.setOnClickListener(this);
        iv_emoticons_checked.setOnClickListener(this);
        // position = getIntent().getIntExtra("position", -1);
        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
        // 判断单聊还是群聊
        chatType = getIntent().getIntExtra("chatType", CHATTYPE_SINGLE);

        if (chatType == CHATTYPE_SINGLE) { // 单聊
            toChatUsername = getIntent().getStringExtra("userId");
            if (getIntent().getStringExtra("userName") != null && !TextUtils.isEmpty(getIntent().getStringExtra("userName"))) {
                ((TextView) findViewById(R.id.name)).setText(getIntent().getStringExtra("userName"));
            } else {
                ((TextView) findViewById(R.id.name)).setText(toChatUsername);
            }
            // conversation =
            // EMChatManager.getInstance().getConversation(toChatUsername,false);
        } else {
            // 群聊
            findViewById(R.id.container_to_group).setVisibility(View.VISIBLE);
            findViewById(R.id.container_remove).setVisibility(View.GONE);
            findViewById(R.id.container_voice_call).setVisibility(View.GONE);
            findViewById(R.id.container_video_call).setVisibility(View.GONE);
            toChatUsername = getIntent().getStringExtra("groupId");
            group = EMGroupManager.getInstance().getGroup(toChatUsername);
            if (group != null)
                ((TextView) findViewById(R.id.name)).setText(group.getGroupName());
            else
                ((TextView) findViewById(R.id.name)).setText(toChatUsername);
            // conversation =
            // EMChatManager.getInstance().getConversation(toChatUsername,true);
        }
        conversation = EMChatManager.getInstance().getConversation(toChatUsername);

        // 把此会话的未读数置为0		
        conversation.resetUnreadMsgCount();
        setCountTime();
        // 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
        // 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
        final List<EMMessage> msgs = conversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            if (chatType == CHATTYPE_SINGLE) {
                conversation.loadMoreMsgFromDB(msgId, pagesize);
            } else {
                conversation.loadMoreGroupMsgFromDB(msgId, pagesize);
            }
        }
        adapter = new MessageAdapter(this, toChatUsername, chatType);
        // 显示消息
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new ListScrollListener());
        adapter.refreshSelectLast();

        listView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                more.setVisibility(View.GONE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.INVISIBLE);
                emojiIconContainer.setVisibility(View.GONE);
                btnContainer.setVisibility(View.GONE);
                return false;
            }
        });

        // 监听当前会话的群聊解散被T事件
        //groupListener = new GroupListener();
        //EMGroupManager.getInstance().addGroupChangeListener(groupListener);

        // show forward message if the message is not null
        String forward_msg_id = getIntent().getStringExtra("forward_msg_id");
        if (forward_msg_id != null) {
            // 显示发送要转发的消息
            forwardMessage(forward_msg_id);
        }
    }

    private void setCountTime() {
        //TODO 这里逻辑不正确，当前取的是聊天记录的首次聊天时间进行倒计时计算，待修改
        long nowTime = DateTime.now(TimeZone.getDefault()).getMilliseconds(TimeZone.getDefault());
        long dTime = 0;
        long lastConversationTime = 0;
        if (conversation.getLastMessage() != null) {
            lastConversationTime = conversation.getLastMessage().getMsgTime();
            dTime = nowTime - lastConversationTime;
        } else {
            dTime = 0;
        }
        Log.d("@@", "lastConversationTime : " + lastConversationTime + "   nowTime : " + nowTime);
        if (dTime == 0) {
            countDownTime.setText("48:00后关闭");
            rl_bottom.setVisibility(View.VISIBLE);
            btnAsk.setVisibility(View.GONE);
            //判断是否第一次进来，如果是，则弹出提示让患者描述其基本情况
            DialogUtils.showAlertDialog(this, "请详细描述您的症状、疾病和身体状况，我们会尽快安排相关医师来回答并保证您的隐私安全。");
        } else if (dTime / 1000 > LIMIE_TIME) {
            countDownTime.setText("该提问已结束");
            rl_bottom.setVisibility(View.GONE);
            btnAsk.setVisibility(View.VISIBLE);
        } else {
            long hour = (LIMIE_TIME - dTime / 1000) / 60 / 60;
            String min = ((LIMIE_TIME - dTime / 1000) - hour * 60 * 60) / 60 + "";
            if (min.length() == 1) {
                min = "0" + min;
            } else if (min.length() == 0) {
                min = "00";
            } else if (min.length() > 2) {
                min = min.substring(0, 2);
            }
            countDownTime.setText("" + hour + ":" + min + "后关闭");
            UIhandler.sendEmptyMessageDelayed(10, 60 * 1000);
            rl_bottom.setVisibility(View.VISIBLE);
            btnAsk.setVisibility(View.GONE);
        }
    }

    private Handler UIhandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setCountTime();
        }

    };

    /**
     * onActivityResult
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CODE_EXIT_GROUP) {
            setResult(RESULT_OK);
            finish();
            return;
        }
        if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
            switch (resultCode) {
            case RESULT_CODE_COPY: // 复制消息
                EMMessage copyMsg = ((EMMessage) adapter.getItem(data.getIntExtra("position", -1)));
                // clipboard.setText(SmileUtils.getSmiledText(ChatActivity.this,
                // ((TextMessageBody) copyMsg.getBody()).getMessage()));
                clipboard.setText(((TextMessageBody) copyMsg.getBody()).getMessage());
                break;
            case RESULT_CODE_DELETE: // 删除消息
                EMMessage deleteMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", -1));
                conversation.removeMessage(deleteMsg.getMsgId());
                adapter.refreshSeekTo(data.getIntExtra("position", adapter.getCount()) - 1);
                break;

            case RESULT_CODE_FORWARD: // 转发消息
                //				EMMessage forwardMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", 0));
                //				Intent intent = new Intent(this, ForwardMessageActivity.class);
                //				intent.putExtra("forward_msg_id", forwardMsg.getMsgId());
                //				startActivity(intent);

                break;

            default:
                break;
            }
        }
        if (resultCode == RESULT_OK) { // 清空消息
            if (requestCode == REQUEST_CODE_EMPTY_HISTORY) {
                // 清空会话
                EMChatManager.getInstance().clearConversation(toChatUsername);
                adapter.refresh();
            } else if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
                if (cameraFile != null && cameraFile.exists())
                    sendPicture(cameraFile.getAbsolutePath());
            } else if (requestCode == REQUEST_CODE_SELECT_VIDEO) { // 发送本地选择的视频

                int duration = data.getIntExtra("dur", 0);
                String videoPath = data.getStringExtra("path");
                File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
                Bitmap bitmap = null;
                FileOutputStream fos = null;
                try {
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
                    if (bitmap == null) {
                        EMLog.d("chatactivity", "problem load video thumbnail bitmap,use default icon");
                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_panel_video_icon);
                    }
                    fos = new FileOutputStream(file);

                    bitmap.compress(CompressFormat.JPEG, 100, fos);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        fos = null;
                    }
                    if (bitmap != null) {
                        bitmap.recycle();
                        bitmap = null;
                    }

                }
                sendVideo(videoPath, file.getAbsolutePath(), duration / 1000);

            } else if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        sendPicByUri(selectedImage);
                    }
                }
            } else if (requestCode == REQUEST_CODE_SELECT_FILE) { // 发送选择的文件
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        sendFile(uri);
                    }
                }

            } else if (requestCode == REQUEST_CODE_MAP) { // 地图
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                String locationAddress = data.getStringExtra("address");
                if (locationAddress != null && !locationAddress.equals("")) {
                    more(more);
                    sendLocationMsg(latitude, longitude, "", locationAddress);
                } else {
                    String st = getResources().getString(R.string.unable_to_get_loaction);
                    Toast.makeText(this, st, Toast.LENGTH_SHORT).show();
                }
                // 重发消息
            } else if (requestCode == REQUEST_CODE_TEXT || requestCode == REQUEST_CODE_VOICE || requestCode == REQUEST_CODE_PICTURE || requestCode == REQUEST_CODE_LOCATION
                    || requestCode == REQUEST_CODE_VIDEO || requestCode == REQUEST_CODE_FILE) {
                resendMessage();
            } else if (requestCode == REQUEST_CODE_COPY_AND_PASTE) {
                // 粘贴
                if (!TextUtils.isEmpty(clipboard.getText())) {
                    String pasteText = clipboard.getText().toString();
                    if (pasteText.startsWith(COPY_IMAGE)) {
                        // 把图片前缀去掉，还原成正常的path
                        sendPicture(pasteText.replace(COPY_IMAGE, ""));
                    }

                }
            } else if (requestCode == REQUEST_CODE_ADD_TO_BLACKLIST) { // 移入黑名单
                EMMessage deleteMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", -1));
                addUserToBlacklist(deleteMsg.getFrom());
            } else if (conversation.getMsgCount() > 0) {
                adapter.refresh();
                setResult(RESULT_OK);
            } else if (requestCode == REQUEST_CODE_GROUP_DETAIL) {
                adapter.refresh();
            }
        }
    }

    /**
     * 消息图标点击事件
     * 
     * @param view
     */
    @Override
    public void onClick(View view) {
        String st1 = getResources().getString(R.string.not_connect_to_server);
        int id = view.getId();
        if (id == R.id.btn_send) {// 点击发送按钮(发文字和表情)
            String s = mEditTextContent.getText().toString();
            sendText(s);
        } else if (id == R.id.btn_take_picture) {
            selectPicFromCamera();// 点击照相图标
        } else if (id == R.id.btn_picture) {
            selectPicFromLocal(); // 点击图片图标
        } else if (id == R.id.btn_location) { // 位置
            //startActivityForResult(new Intent(this, BaiduMapActivity.class), REQUEST_CODE_MAP);
        } else if (id == R.id.iv_emoticons_normal) { // 点击显示表情框
            more.setVisibility(View.VISIBLE);
            iv_emoticons_normal.setVisibility(View.INVISIBLE);
            iv_emoticons_checked.setVisibility(View.VISIBLE);
            btnContainer.setVisibility(View.GONE);
            emojiIconContainer.setVisibility(View.VISIBLE);
            hideKeyboard();
        } else if (id == R.id.iv_emoticons_checked) { // 点击隐藏表情框
            iv_emoticons_normal.setVisibility(View.VISIBLE);
            iv_emoticons_checked.setVisibility(View.INVISIBLE);
            btnContainer.setVisibility(View.VISIBLE);
            emojiIconContainer.setVisibility(View.GONE);
            more.setVisibility(View.GONE);

        } else if (id == R.id.btn_video) {
            // 点击摄像图标
            //Intent intent = new Intent(ChatActivity.this, ImageGridActivity.class);
            //startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
        } else if (id == R.id.btn_file) { // 点击文件图标
            selectFileFromLocal();
        } else if (id == R.id.btn_voice_call) { // 点击语音电话图标
            //			if (!EMChatManager.getInstance().isConnected())
            //				Toast.makeText(this, st1, 0).show();
            //			else
            //				startActivity(new Intent(ChatActivity.this, VoiceCallActivity.class).putExtra("username", toChatUsername)
            //				        .putExtra("isComingCall", false));
        } else if (id == R.id.btn_video_call) { //视频通话
            //		    if (!EMChatManager.getInstance().isConnected())
            //                Toast.makeText(this, st1, 0).show();
            //            else
            //                startActivity(new Intent(this, VideoCallActivity.class).putExtra("username", toChatUsername)
            //                        .putExtra("isComingCall", false));
        } else if (id == R.id.ll_take_pic) {
            selectPicFromCamera();// 点击照相图标
            dimissPickerPicDialog();
        } else if (id == R.id.ll_select_pic) {
            selectPicFromLocal();
            dimissPickerPicDialog();
        } else if (id == R.id.btnAsk) {
            //我要问医生，现在处理是回到问答首页
            startActivity(new Intent(this, HomeActivity.class).putExtra("Tab", 2));
        }
    }

    /**
     * 事件监听
     * 
     * see {@link EMNotifierEvent}
     */
    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
        case EventNewMessage: {
            //获取到message
            EMMessage message = (EMMessage) event.getData();

            String arg1 = message.getStringAttribute("attribute1", "");
            String arg2 = message.getStringAttribute("attribute2", "");
            Log.d("@@", "arg1 : " + arg1);
            Log.d("@@", "arg2 : " + arg2);
            String username = null;
            //群组消息
            if (message.getChatType() == ChatType.GroupChat) {
                username = message.getTo();
            } else {
                //单聊消息
                username = message.getFrom();
            }

            //如果是当前会话的消息，刷新聊天页面
            if (username.equals(getToChatUsername())) {
                refreshUIWithNewMessage();
                //声音和震动提示有新消息
                HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(message);
            } else {
                //如果消息不是和当前聊天ID的消息
                HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
            }

            break;
        }
        case EventDeliveryAck: {
            //获取到message
            EMMessage message = (EMMessage) event.getData();
            refreshUI();
            break;
        }
        case EventReadAck: {
            //获取到message
            EMMessage message = (EMMessage) event.getData();
            refreshUI();
            break;
        }
        case EventOfflineMessage: {
            //a list of offline messages 
            //List<EMMessage> offlineMessages = (List<EMMessage>) event.getData();
            refreshUI();
            break;
        }
        default:
            break;
        }

    }

    private void refreshUIWithNewMessage() {
        runOnUiThread(new Runnable() {
            public void run() {
                adapter.refreshSelectLast();
                setCountTime();
            }
        });
    }

    private void refreshUI() {
        runOnUiThread(new Runnable() {
            public void run() {
                adapter.refresh();
            }
        });
    }

    /**
     * 照相获取图片
     */
    public void selectPicFromCamera() {
        if (!CommonUtils.isExitsSdcard()) {
            String st = getResources().getString(R.string.sd_card_does_not_exist);
            Toast.makeText(getApplicationContext(), st, Toast.LENGTH_SHORT).show();
            return;
        }

        cameraFile = new File(PathUtil.getInstance().getImagePath(), TpqApplication.getInstance().getUserName() + System.currentTimeMillis() + ".jpg");
        cameraFile.getParentFile().mkdirs();
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)), REQUEST_CODE_CAMERA);
    }

    /**
     * 选择文件
     */
    private void selectFileFromLocal() {
        Intent intent = null;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
    }

    /**
     * 从图库获取图片
     */
    public void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }

    /**
     * 发送文本消息
     * 
     * @param content
     *            message content
     * @param:isResend
     *            boolean resend
     */
    private void sendText(String content) {
        //MobclickAgent.onEvent(this,"SendText");
        if (content.length() > 0) {
            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
            // 如果是群聊，设置chattype,默认是单
            if (chatType == CHATTYPE_GROUP)
                message.setChatType(ChatType.GroupChat);

            // 增加自己特定的属性,目前sdk支持int,boolean,String这三种属性，可以设置多个扩展属性
            message.setAttribute("attribute1", "自己，男，25岁");
            message.setAttribute("attribute2", "屁股痛");
            //如果患者初始信息带有图片，则分开两次发送，第一次发送自定义属性，第二次发送图片

            TextMessageBody txtBody = new TextMessageBody(content);
            // 设置消息body
            message.addBody(txtBody);
            // 设置要发给谁,用户username或者群聊groupid
            message.setReceipt(toChatUsername);
            // 把messgage加到conversation中
            conversation.addMessage(message);
            // 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
            adapter.refreshSelectLast();
            mEditTextContent.setText("");

            setResult(RESULT_OK);

        }
    }

    /**
     * 发送语音
     * 
     * @param filePath
     * @param fileName
     * @param length
     * @param isResend
     */
    private void sendVoice(String filePath, String fileName, String length, boolean isResend) {
        if (!(new File(filePath).exists())) {
            return;
        }
        try {
            final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
            // 如果是群聊，设置chattype,默认是单聊
            if (chatType == CHATTYPE_GROUP)
                message.setChatType(ChatType.GroupChat);
            message.setReceipt(toChatUsername);
            int len = Integer.parseInt(length);
            VoiceMessageBody body = new VoiceMessageBody(new File(filePath), len);
            message.addBody(body);

            conversation.addMessage(message);
            adapter.refreshSelectLast();
            setResult(RESULT_OK);
            // send file
            // sendVoiceSub(filePath, fileName, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送图片
     * 
     * @param filePath
     */
    private void sendPicture(final String filePath) {
        //MobclickAgent.onEvent(this,"SendPicture");
        String to = toChatUsername;
        // create and add image message in view
        final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
        // 如果是群聊，设置chattype,默认是单聊
        if (chatType == CHATTYPE_GROUP)
            message.setChatType(ChatType.GroupChat);

        message.setReceipt(to);
        ImageMessageBody body = new ImageMessageBody(new File(filePath));
        // 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
        // body.setSendOriginalImage(true);
        message.addBody(body);
        conversation.addMessage(message);

        listView.setAdapter(adapter);
        adapter.refreshSelectLast();
        setResult(RESULT_OK);
        // more(more);
    }

    /**
     * 发送视频消息
     */
    private void sendVideo(final String filePath, final String thumbPath, final int length) {
        final File videoFile = new File(filePath);
        if (!videoFile.exists()) {
            return;
        }
        try {
            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VIDEO);
            // 如果是群聊，设置chattype,默认是单聊
            if (chatType == CHATTYPE_GROUP)
                message.setChatType(ChatType.GroupChat);
            String to = toChatUsername;
            message.setReceipt(to);
            VideoMessageBody body = new VideoMessageBody(videoFile, thumbPath, length, videoFile.length());
            message.addBody(body);
            conversation.addMessage(message);
            listView.setAdapter(adapter);
            adapter.refreshSelectLast();
            setResult(RESULT_OK);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据图库图片uri发送图片
     * 
     * @param selectedImage
     */
    private void sendPicByUri(Uri selectedImage) {
        // String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
        String st8 = getResources().getString(R.string.cant_find_pictures);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex("_data");
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            sendPicture(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;

            }
            sendPicture(file.getAbsolutePath());
        }

    }

    /**
     * 发送位置信息
     * 
     * @param latitude
     * @param longitude
     * @param imagePath
     * @param locationAddress
     */
    private void sendLocationMsg(double latitude, double longitude, String imagePath, String locationAddress) {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.LOCATION);
        // 如果是群聊，设置chattype,默认是单聊
        if (chatType == CHATTYPE_GROUP)
            message.setChatType(ChatType.GroupChat);
        LocationMessageBody locBody = new LocationMessageBody(locationAddress, latitude, longitude);
        message.addBody(locBody);
        message.setReceipt(toChatUsername);
        conversation.addMessage(message);
        listView.setAdapter(adapter);
        adapter.refreshSelectLast();
        setResult(RESULT_OK);

    }

    /**
     * 发送文件
     * 
     * @param uri
     */
    private void sendFile(Uri uri) {
        String filePath = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        }
        File file = new File(filePath);
        if (file == null || !file.exists()) {
            String st7 = getResources().getString(R.string.File_does_not_exist);
            Toast.makeText(getApplicationContext(), st7, Toast.LENGTH_SHORT).show();
            return;
        }
        if (file.length() > 10 * 1024 * 1024) {
            String st6 = getResources().getString(R.string.The_file_is_not_greater_than_10_m);
            Toast.makeText(getApplicationContext(), st6, Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建一个文件消息
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.FILE);
        // 如果是群聊，设置chattype,默认是单聊
        if (chatType == CHATTYPE_GROUP)
            message.setChatType(ChatType.GroupChat);

        message.setReceipt(toChatUsername);
        // add message body
        NormalFileMessageBody body = new NormalFileMessageBody(new File(filePath));
        message.addBody(body);
        conversation.addMessage(message);
        listView.setAdapter(adapter);
        adapter.refreshSelectLast();
        setResult(RESULT_OK);
    }

    /**
     * 重发消息
     */
    private void resendMessage() {
        EMMessage msg = null;
        msg = conversation.getMessage(resendPos);
        // msg.setBackSend(true);
        msg.status = EMMessage.Status.CREATE;

        adapter.refreshSeekTo(resendPos);
    }

    /**
     * 显示语音图标按钮
     * 
     * @param view
     */
    public void setModeVoice(View view) {
        hideKeyboard();
        edittext_layout.setVisibility(View.GONE);
        more.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        buttonSetModeKeyboard.setVisibility(View.VISIBLE);
        buttonSend.setVisibility(View.GONE);
        btnMore.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.VISIBLE);
        iv_emoticons_normal.setVisibility(View.VISIBLE);
        iv_emoticons_checked.setVisibility(View.INVISIBLE);
        btnContainer.setVisibility(View.VISIBLE);
        emojiIconContainer.setVisibility(View.GONE);

    }

    /**
     * 显示键盘图标
     * 
     * @param view
     */
    public void setModeKeyboard(View view) {
        // mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener()
        // {
        // @Override
        // public void onFocusChange(View v, boolean hasFocus) {
        // if(hasFocus){
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        // }
        // }
        // });
        edittext_layout.setVisibility(View.VISIBLE);
        more.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        buttonSetModeVoice.setVisibility(View.VISIBLE);
        // mEditTextContent.setVisibility(View.VISIBLE);
        mEditTextContent.requestFocus();
        // buttonSend.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.GONE);
        if (TextUtils.isEmpty(mEditTextContent.getText())) {
            btnMore.setVisibility(View.VISIBLE);
            buttonSend.setVisibility(View.GONE);
        } else {
            btnMore.setVisibility(View.GONE);
            buttonSend.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 点击清空聊天记录
     * 
     * @param view
     */
    public void emptyHistory(View view) {
        String st5 = getResources().getString(R.string.Whether_to_empty_all_chats);
        startActivityForResult(new Intent(this, AlertDialog.class).putExtra("titleIsCancel", true).putExtra("msg", st5).putExtra("cancel", true), REQUEST_CODE_EMPTY_HISTORY);
    }

    /**
     * 点击进入群组详情
     * 医生评价评价入口
     * 
     * @param view
     */
    public void toGroupDetails(View view) {
        //	    if(group == null){
        //	        Toast.makeText(getApplicationContext(), R.string.gorup_not_found, 0).show();
        //	        return;
        //	    }
        //		startActivityForResult((new Intent(this, GroupDetailsActivity.class).putExtra("groupId", toChatUsername)),
        //				REQUEST_CODE_GROUP_DETAIL);

        startActivity(new Intent(this, EvaluationOfDoctorActivity.class).putExtra("doctorId", toChatUsername));
    }

    /**
     * 显示或隐藏图标按钮页
     * 
     * @param view
     */
    public void more(View view) {
        //		if (more.getVisibility() == View.GONE) {
        //			System.out.println("more gone");
        //			hideKeyboard();
        //			more.setVisibility(View.VISIBLE);
        //			btnContainer.setVisibility(View.VISIBLE);
        //			emojiIconContainer.setVisibility(View.GONE);
        //		} else {
        //			if (emojiIconContainer.getVisibility() == View.VISIBLE) {
        //				emojiIconContainer.setVisibility(View.GONE);
        //				btnContainer.setVisibility(View.VISIBLE);
        //				iv_emoticons_normal.setVisibility(View.VISIBLE);
        //				iv_emoticons_checked.setVisibility(View.INVISIBLE);
        //			} else {
        //				more.setVisibility(View.GONE);
        //			}
        //
        //		}
        showPickerPicDialog();

    }

    /**
     * 点击文字输入框
     * 
     * @param v
     */
    public void editClick(View v) {
        listView.setSelection(listView.getCount() - 1);
        if (more.getVisibility() == View.VISIBLE) {
            more.setVisibility(View.GONE);
            iv_emoticons_normal.setVisibility(View.VISIBLE);
            iv_emoticons_checked.setVisibility(View.INVISIBLE);
        }

    }

    private PowerManager.WakeLock wakeLock;

    /**
     * 按住说话listener
     * 
     */
    //	class PressToSpeakListen implements View.OnTouchListener {
    //		@Override
    //		public boolean onTouch(View v, MotionEvent event) {
    //			switch (event.getAction()) {
    //			case MotionEvent.ACTION_DOWN:
    //				if (!CommonUtils.isExitsSdcard()) {
    //					String st4 = getResources().getString(R.string.Send_voice_need_sdcard_support);
    //					Toast.makeText(ChatActivity.this, st4, Toast.LENGTH_SHORT).show();
    //					return false;
    //				}
    //				try {
    //					v.setPressed(true);
    //					wakeLock.acquire();
    //					if (VoicePlayClickListener.isPlaying)
    //						VoicePlayClickListener.currentPlayListener.stopPlayVoice();
    //					recordingContainer.setVisibility(View.VISIBLE);
    //					recordingHint.setText(getString(R.string.move_up_to_cancel));
    //					recordingHint.setBackgroundColor(Color.TRANSPARENT);
    //					voiceRecorder.startRecording(null, toChatUsername, getApplicationContext());
    //				} catch (Exception e) {
    //					e.printStackTrace();
    //					v.setPressed(false);
    //					if (wakeLock.isHeld())
    //						wakeLock.release();
    //					if (voiceRecorder != null)
    //						voiceRecorder.discardRecording();
    //					recordingContainer.setVisibility(View.INVISIBLE);
    //					Toast.makeText(ChatActivity.this, R.string.recoding_fail, Toast.LENGTH_SHORT).show();
    //					return false;
    //				}
    //
    //				return true;
    //			case MotionEvent.ACTION_MOVE: {
    //				if (event.getY() < 0) {
    //					recordingHint.setText(getString(R.string.release_to_cancel));
    //					recordingHint.setBackgroundResource(R.drawable.recording_text_hint_bg);
    //				} else {
    //					recordingHint.setText(getString(R.string.move_up_to_cancel));
    //					recordingHint.setBackgroundColor(Color.TRANSPARENT);
    //				}
    //				return true;
    //			}
    //			case MotionEvent.ACTION_UP:
    //				v.setPressed(false);
    //				recordingContainer.setVisibility(View.INVISIBLE);
    //				if (wakeLock.isHeld())
    //					wakeLock.release();
    //				if (event.getY() < 0) {
    //					// discard the recorded audio.
    //					voiceRecorder.discardRecording();
    //
    //				} else {
    //					// stop recording and send voice file
    //					String st1 = getResources().getString(R.string.Recording_without_permission);
    //					String st2 = getResources().getString(R.string.The_recording_time_is_too_short);
    //					String st3 = getResources().getString(R.string.send_failure_please);
    //					try {
    //						int length = voiceRecorder.stopRecoding();
    //						if (length > 0) {
    //							sendVoice(voiceRecorder.getVoiceFilePath(), voiceRecorder.getVoiceFileName(toChatUsername),
    //									Integer.toString(length), false);
    //						} else if (length == EMError.INVALID_FILE) {
    //							Toast.makeText(getApplicationContext(), st1, Toast.LENGTH_SHORT).show();
    //						} else {
    //							Toast.makeText(getApplicationContext(), st2, Toast.LENGTH_SHORT).show();
    //						}
    //					} catch (Exception e) {
    //						e.printStackTrace();
    //						Toast.makeText(ChatActivity.this, st3, Toast.LENGTH_SHORT).show();
    //					}
    //
    //				}
    //				return true;
    //			default:
    //				recordingContainer.setVisibility(View.INVISIBLE);
    //				if (voiceRecorder != null)
    //					voiceRecorder.discardRecording();
    //				return false;
    //			}
    //		}
    //	}

    /**
     * 获取表情的gridview的子view
     * 
     * @param i
     * @return
     */
    private View getGridChildView(int i) {
        View view = View.inflate(this, R.layout.expression_gridview, null);
        ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
        List<String> list = new ArrayList<String>();
        if (i == 1) {
            List<String> list1 = reslist.subList(0, 20);
            list.addAll(list1);
        } else if (i == 2) {
            list.addAll(reslist.subList(20, reslist.size()));
        }
        list.add("delete_expression");
        final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this, 1, list);
        gv.setAdapter(expressionAdapter);
        gv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filename = expressionAdapter.getItem(position);
                try {
                    // 文字输入框可见时，才可输入表情
                    // 按住说话可见，不让输入表情
                    if (buttonSetModeKeyboard.getVisibility() != View.VISIBLE) {

                        if (filename != "delete_expression") { // 不是删除键，显示表情
                            // 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
                            Class clz = Class.forName("com.zcmedical.huanxin.SmileUtils");
                            Field field = clz.getField(filename);
                            mEditTextContent.append(SmileUtils.getSmiledText(ChatActivity.this, (String) field.get(null)));
                        } else { // 删除文字或者表情
                            if (!TextUtils.isEmpty(mEditTextContent.getText())) {

                                int selectionStart = mEditTextContent.getSelectionStart();// 获取光标的位置
                                if (selectionStart > 0) {
                                    String body = mEditTextContent.getText().toString();
                                    String tempStr = body.substring(0, selectionStart);
                                    int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
                                    if (i != -1) {
                                        CharSequence cs = tempStr.substring(i, selectionStart);
                                        if (SmileUtils.containsKey(cs.toString()))
                                            mEditTextContent.getEditableText().delete(i, selectionStart);
                                        else
                                            mEditTextContent.getEditableText().delete(selectionStart - 1, selectionStart);
                                    } else {
                                        mEditTextContent.getEditableText().delete(selectionStart - 1, selectionStart);
                                    }
                                }
                            }

                        }
                    }
                } catch (Exception e) {
                }

            }
        });
        return view;
    }

    public List<String> getExpressionRes(int getSum) {
        List<String> reslist = new ArrayList<String>();
        for (int x = 1; x <= getSum; x++) {
            String filename = "ee_" + x;

            reslist.add(filename);

        }
        return reslist;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
        //		EMGroupManager.getInstance().removeGroupChangeListener(groupListener);
    }

    @Override
    protected void onResume() {
        Log.i("ChatActivity", "onResume");
        super.onResume();
        //MobclickAgent.onResume(this);
        if (group != null)
            ((TextView) findViewById(R.id.name)).setText(group.getGroupName());
        adapter.refresh();

        DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();
        sdkHelper.pushActivity(this);
        // register the event listener when enter the foreground
        EMChatManager.getInstance().registerEventListener(this, new EMNotifierEvent.Event[] { EMNotifierEvent.Event.EventNewMessage

        , EMNotifierEvent.Event.EventDeliveryAck, EMNotifierEvent.Event.EventReadAck });
    }

    @Override
    protected void onStop() {
        Log.i("ChatActivity", "onStop");

        // unregister this event listener when this activity enters the background
        EMChatManager.getInstance().unregisterEventListener(this);

        DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();

        // 把此activity 从foreground activity 列表里移除
        sdkHelper.popActivity(this);

        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //MobclickAgent.onPause(this);
        if (wakeLock.isHeld())
            wakeLock.release();
        //		if (VoicePlayClickListener.isPlaying && VoicePlayClickListener.currentPlayListener != null) {
        //			// 停止语音播放
        //			VoicePlayClickListener.currentPlayListener.stopPlayVoice();
        //		}

        try {
            // 停止录音
            if (voiceRecorder.isRecording()) {
                voiceRecorder.discardRecording();
                recordingContainer.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 加入到黑名单
     * 
     * @param username
     */
    private void addUserToBlacklist(final String username) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.Is_moved_into_blacklist));
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMContactManager.getInstance().addUserToBlackList(username, false);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), R.string.Move_into_blacklist_success, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (EaseMobException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), R.string.Move_into_blacklist_failure, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 返回
     * 
     * @param view
     */
    public void back(View view) {
        finish();
    }

    /**
     * 覆盖手机返回键
     */
    @Override
    public void onBackPressed() {
        if (more.getVisibility() == View.VISIBLE) {
            more.setVisibility(View.GONE);
            iv_emoticons_normal.setVisibility(View.VISIBLE);
            iv_emoticons_checked.setVisibility(View.INVISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * listview滑动监听listener
     * 
     */
    private class ListScrollListener implements OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
            case OnScrollListener.SCROLL_STATE_IDLE:
                if (view.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
                    isloading = true;
                    loadmorePB.setVisibility(View.VISIBLE);
                    // sdk初始化加载的聊天记录为20条，到顶时去db里获取更多					
                    List<EMMessage> messages;
                    EMMessage firstMsg = conversation.getAllMessages().get(0);
                    try {
                        // 获取更多messges，调用此方法的时候从db获取的messages
                        // sdk会自动存入到此conversation中
                        if (chatType == CHATTYPE_SINGLE)
                            messages = conversation.loadMoreMsgFromDB(firstMsg.getMsgId(), pagesize);
                        else
                            messages = conversation.loadMoreGroupMsgFromDB(firstMsg.getMsgId(), pagesize);
                    } catch (Exception e1) {
                        loadmorePB.setVisibility(View.GONE);
                        return;
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                    }
                    if (messages.size() != 0) {
                        // 刷新ui
                        if (messages.size() > 0) {
                            adapter.refreshSeekTo(messages.size() - 1);
                        }
                        if (messages.size() != pagesize)
                            haveMoreData = false;
                    } else {
                        haveMoreData = false;
                    }
                    loadmorePB.setVisibility(View.GONE);
                    isloading = false;

                }
                break;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        // 点击notification bar进入聊天页面，保证只有一个聊天页面
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }

    }

    /**
     * 转发消息
     * 
     * @param forward_msg_id
     */
    protected void forwardMessage(String forward_msg_id) {
        EMMessage forward_msg = EMChatManager.getInstance().getMessage(forward_msg_id);
        EMMessage.Type type = forward_msg.getType();
        switch (type) {
        case TXT:
            // 获取消息内容，发送消息
            String content = ((TextMessageBody) forward_msg.getBody()).getMessage();
            sendText(content);
            break;
        case IMAGE:
            // 发送图片
            String filePath = ((ImageMessageBody) forward_msg.getBody()).getLocalUrl();
            if (filePath != null) {
                File file = new File(filePath);
                if (!file.exists()) {
                    // 不存在大图发送缩略图
                    filePath = ImageUtil.getThumbnailImagePath(filePath);
                }
                sendPicture(filePath);
            }
            break;
        default:
            break;
        }
    }

    /**
     * 监测群组解散或者被T事件
     * 
     */
    //	class GroupListener extends GroupReomveListener {
    //
    //		@Override
    //		public void onUserRemoved(final String groupId, String groupName) {
    //			runOnUiThread(new Runnable() {
    //				String st13 = getResources().getString(R.string.you_are_group);
    //				public void run() {
    //					if (toChatUsername.equals(groupId)) {
    //						Toast.makeText(ChatActivity.this, st13, 1).show();
    //						if (GroupDetailsActivity.instance != null)
    //							GroupDetailsActivity.instance.finish();
    //						finish();
    //					}
    //				}
    //			});
    //		}
    //
    //		@Override
    //		public void onGroupDestroy(final String groupId, String groupName) {
    //			// 群组解散正好在此页面，提示群组被解散，并finish此页面
    //			runOnUiThread(new Runnable() {
    //				String st14 = getResources().getString(R.string.the_current_group);
    //				public void run() {
    //					if (toChatUsername.equals(groupId)) {
    //						Toast.makeText(ChatActivity.this, st14, 1).show();
    //						if (GroupDetailsActivity.instance != null)
    //							GroupDetailsActivity.instance.finish();
    //						finish();
    //					}
    //				}
    //			});
    //		}
    //
    //	}

    public String getToChatUsername() {
        return toChatUsername;
    }

    public ListView getListView() {
        return listView;
    }

    private BasicDialog pickerPicDialog;
    /** 相机拍照request code **/
    public static final int REQUESTCODE_TAKE_PHOTO = 1;
    /** 相册选择图片request code **/
    public static final int REQUESTCODE_PICK_PHOTO = 2;

    private void showPickerPicDialog() {
        if (null == pickerPicDialog) {
            BasicDialog.Builder bBuilder = new BasicDialog.Builder(getContext());
            View contentView = LayoutInflater.from(getContext()).inflate(R.layout.view_picker_pic, null);
            contentView.findViewById(R.id.ll_take_pic).setOnClickListener(this);
            contentView.findViewById(R.id.ll_select_pic).setOnClickListener(this);

            bBuilder.setContentView(contentView);
            bBuilder.setTitle(getContext().getString(R.string.add_pic_dialog_title));

            pickerPicDialog = bBuilder.create();
        }
        pickerPicDialog.show();

    }

    private void dimissPickerPicDialog() {
        if (null != pickerPicDialog && pickerPicDialog.isShowing()) {
            pickerPicDialog.dismiss();
        }
    }

}
