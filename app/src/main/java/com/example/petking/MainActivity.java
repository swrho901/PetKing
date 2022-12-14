package com.example.petking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.app.TabActivity;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements MyRecyclerAdapter.MyRecyclerViewClickListener, SimpleTextAdapter.SimpleTextClickListener, OnMapReadyCallback {
    // , SimpleTextAdapter.SimpleTextClickListener
    ArrayList<ItemData> dataList = new ArrayList<>();
    int[] cat = {R.drawable.doggy, R.drawable.dog3,  R.drawable.dog2, R.drawable.cat1, R.drawable.cat2};

    // ????????? ?????? ????????? ?????? ???????????? ?????? ?????????
    ArrayList<ItemData> search_list = new ArrayList<>();
    EditText editText;

    //final Geocoder geocoder = new Geocoder(this);

    final MyRecyclerAdapter adapter = new MyRecyclerAdapter(dataList);
    static int i=0;

    private boolean fabMain_status = false;
    private FloatingActionButton fabMain;
    private FloatingActionButton fabCamera;
    private FloatingActionButton fabEdit;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    public int total_num_of_contents = 4;

    public Context c;
    public Context[] Carr = new Context[10000];
    public String[] addArr = new String[10000];
    public String[] contextArr = new String[10000];
    public String[] typeOfContextArr = new String[10000];
    public String[] idArr = new String[10000];
    public String[] currentStatArr = new String[10000];
    public int[] likeNumArr = new int[10000];
    public int[] moneyArr = new int[10000];
    public int[] negoArr = new int[10000];
    public String[] titleArr = new String[10000];
    public int[] total_read_numArr = new int[10000];

    public String[] ArrId = new String[10000];
    public String[] ArrTitle = new String[10000];
    public String[] ArrCtx = new String[10000];

    public int contextArrIdx = 0;
    public int commArrIdx = 0;


    public String user_address, user_id;
    public Uri tempUri;
    ImageView load;
    public int totalContextNum = 0;

    private NotificationManager mNotificationManager;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // editText ????????? ??????
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = editText.getText().toString();
                search_list.clear();

                if(searchText.equals("")){
                    adapter.setItems(dataList);
                }
                else {
                    // ?????? ????????? ??????????????? ??????
                    for (int a = 0; a < dataList.size(); a++) {
                        if (dataList.get(a).title.toLowerCase().contains(searchText.toLowerCase())) {
                            search_list.add(dataList.get(a));
                        }
                        adapter.setItems(search_list);
                    }
                }
            }
        });


        // ?????????????????? ?????????????????? ???????????? ?????? ?????? ????????? ???????????? ??????
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        // ?????????????????? ?????????
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(recyclerView.getContext(),new LinearLayoutManager(this).getOrientation());
        // ????????? ??????
        recyclerView.addItemDecoration(dividerItemDecoration);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
        adapter.setOnClickListener(this);

        // ???????????? ??????????????????

        ArrayList<String> list = new ArrayList<>();

        // ????????????????????? LinearLayoutManager ?????? ??????.
        RecyclerView recyclerView2 = findViewById(R.id.recyclerView2) ;
        recyclerView2.setLayoutManager(new LinearLayoutManager(this)) ;

        // ?????????????????? ?????????
        DividerItemDecoration dividerItemDecoration2 =
                new DividerItemDecoration(recyclerView2.getContext(),new LinearLayoutManager(this).getOrientation());
        // ????????? ??????
        recyclerView2.addItemDecoration(dividerItemDecoration2);

        // ????????????????????? SimpleTextAdapter ?????? ??????.
        SimpleTextAdapter adapter2 = new SimpleTextAdapter(list) ;
        recyclerView2.setAdapter(adapter2) ;
        adapter2.setOnClickListener(this::onTextClicked);



   //  ??? ????????? ???????????????????????? ????????? ????????? ???????????? ????????? ?????? ?????? ???

        // ???????????? ??????????????? ????????? ??? ??????
        /*load = (ImageView)findViewById(R.id.testimg);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference pathReference = storageReference.child("photo");
        if (pathReference == null) {
            Toast.makeText(MainActivity.this, "???????????? ????????? ????????????." ,Toast.LENGTH_SHORT).show();
        } else {
            StorageReference submitProfile = storageReference.child("969833");
            submitProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(MainActivity.this).load(uri).into(load);
                    tempUri = uri;

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }*/



        database = FirebaseDatabase.getInstance(); // ?????????????????? ?????????????????? ??????

        fabMain = findViewById(R.id.fabMain);
        fabCamera = findViewById(R.id.fabCamera);
        fabEdit = findViewById(R.id.fabEdit);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser == null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        Button btnLogout = findViewById(R.id.main_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });

        TextView tvName = findViewById(R.id.tvName);

        TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
        tabHost.setup();
        TabHost.TabSpec spec;

        // ?????? ?????? ????????? ??????
        ImageView tabwidget01 = new ImageView(this);
        tabwidget01.setImageResource(R.drawable.ic_baseline_main_24);

        ImageView tabwidget02 = new ImageView(this);
        tabwidget02.setImageResource(R.drawable.ic_baseline_room_24);

        ImageView tabwidget03 = new ImageView(this);
        tabwidget03.setImageResource(R.drawable.ic_baseline_comm_24);

        ImageView tabwidget04 = new ImageView(this);
        tabwidget04.setImageResource(R.drawable.ic_baseline_my_info_24);

        //?????? ????????? ????????? ??? ????????????
        TabHost.TabSpec tabMain = tabHost.newTabSpec("main").setIndicator(tabwidget01);
        tabMain.setContent(R.id.main);
        tabHost.addTab(tabMain);

        TabHost.TabSpec tabChat = tabHost.newTabSpec("chat").setIndicator(tabwidget02);
        tabChat.setContent(R.id.chat);
        tabHost.addTab(tabChat);

        TabHost.TabSpec tabComm = tabHost.newTabSpec("comm").setIndicator(tabwidget03);
        tabComm.setContent(R.id.comm);
        tabHost.addTab(tabComm);

        TabHost.TabSpec tabMyinfo = tabHost.newTabSpec("myinfo").setIndicator(tabwidget04);
        tabMyinfo.setContent(R.id.myinfo);
        tabHost.addTab(tabMyinfo);



        DatabaseReference reference = database.getReference("users").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user != null){
                    tvName.setText(user.name);
                    user_id = user.id;
                    user_address = user.address;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        DatabaseReference mFirebaseRef = database.getReference("context");
        int[] visit = new int[10000000];
        mFirebaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String s) {
                for (DataSnapshot poly : snapshot.getChildren()) {

                    String kk = snapshot.getKey();
                    int v = Integer.parseInt(kk);
                    if(visit[v] == 1) continue;
                    visit[v] = 1;

                    String ididid = snapshot.child("id").getValue(String.class);
                    String adadad = snapshot.child("address").getValue(String.class);
                    String tititi = snapshot.child("title").getValue(String.class);
                    String currentStst = snapshot.child("currentStat").getValue(String.class);
                    int numnumnum = snapshot.child("total_read_num").getValue(Integer.class);
                    int likenunu = snapshot.child("like_number").getValue(Integer.class);
                    int moneymoney = snapshot.child("money").getValue(Integer.class);
                    int negonego = snapshot.child("negotiation").getValue(Integer.class);
                    String main_ctxctx = snapshot.child("main_context").getValue(String.class);
                    String toctoc = snapshot.child("typeOfContext").getValue(String.class);
                    //String toctoc = snapshot.child("typeOfContext").getValue(String.class);

                    total_read_numArr[contextArrIdx] = numnumnum;
                    addArr[contextArrIdx] = adadad; //.substring(10,17) //
                    titleArr[contextArrIdx] = tititi;
                    moneyArr[contextArrIdx] = moneymoney;
                    typeOfContextArr[contextArrIdx] = toctoc;
                    idArr[contextArrIdx] = ididid;
                    currentStatArr[contextArrIdx] = currentStst;
                    likeNumArr[contextArrIdx] = likenunu;
                    negoArr[contextArrIdx] = negonego;
                    contextArr[contextArrIdx] = main_ctxctx;
                    contextArrIdx++;

                    String strKey = poly.getKey();
                    String id=String.valueOf(poly.child(strKey).child("id").getValue());
                    String title=String.valueOf(poly.child("title").getValue());
                    int k = 0;
                    c = snapshot.getValue(Context.class);
                    String addd = adadad.substring(10,17);

                    ImageView load;
                    String str1 = Integer.toString(numnumnum);



                    dataList.add(new ItemData(numnumnum, tititi,toctoc, moneymoney, addd));

                }
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String s) {

            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // string ??????????????? ?????? ?????? ????????? ????????? ???????????? ??? ?????????
        int SIZE = 100000;
        Context[] ctt = {};
        int len = ctt.length;

        DatabaseReference mFirebaseRef2 = database.getReference("community");
        String[] visit2 = new String[1000000];
        mFirebaseRef2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String s) {
                for (DataSnapshot poly : snapshot.getChildren()) {

                    String ididid = snapshot.child("id").getValue(String.class);
                    String mainCtx = snapshot.child("main_context").getValue(String.class);
                    int likenunu = snapshot.child("like_number").getValue(Integer.class);
                    String tititi = snapshot.child("title").getValue(String.class);

                    int contFlag = 0;
                    for(int i=0;i<commArrIdx;i++) {
                        if(visit2[i].equals(tititi)) {
                            contFlag = 1;
                            break;
                        }
                    }
                    if(contFlag == 1)
                        continue;

                    ArrId[commArrIdx] = ididid;
                    ArrTitle[commArrIdx] = tititi;
                    ArrCtx[commArrIdx] = mainCtx;
                    // like num ??? ?????????

                    visit2[commArrIdx] = tititi;
                    commArrIdx++;
                    list.add(tititi + "\n" +ididid);
                }
                recyclerView2.setAdapter(adapter2);
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String s) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        DatabaseReference mFirebaseRef3 = database.getReference("message");

        mFirebaseRef3.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String s) {

                String str = snapshot.getKey();
                Chat ch = snapshot.getValue(Chat.class);

                if(str.contains(user_id)){
                    int idx = str.indexOf(user_id);
                    String user2_id = null;
                    if(idx == 0){
                        int len = user_id.length();
                        user2_id = str.substring(len,str.length());
                    }
                    else{
                        user2_id = str.substring(0,idx);
                    }

                    sendNotification(user_id, user2_id);
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String s) {

                String str = snapshot.getKey();
                Chat ch = snapshot.getValue(Chat.class);

                if(str.contains(user_id)){
                    int idx = str.indexOf(user_id);
                    String user2_id = null;
                    if(idx == 0){
                        int len = user_id.length();
                        user2_id = str.substring(len,str.length());
                    }
                    else{
                        user2_id = str.substring(0,idx);
                    }

                    sendNotification(user_id, user2_id);
                }
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        createNotificationChannel(); // ?????? ?????? ????????? ????????


        // ??????????????? ?????? ??????
        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFab();

            }
        });

        // ????????? ????????? ?????? ??????
        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //Intent intent = new Intent(getApplicationContext(), CommActivity.class);
               Intent intent = new Intent(getApplicationContext(), CommActivity.class);
               intent.putExtra("user_id", user_id);
               startActivity(intent);
               //Toast.makeText(MainActivity.this, "????????? ?????? ??????", Toast.LENGTH_SHORT).show();
            }
        });

        // ?????? ????????? ?????? ??????
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), writeActivity.class);
                intent.putExtra("user_address", user_address);
                intent.putExtra("user_id", user_id);
                startActivity(intent);
            }
        });

    }
    private void logoutUser(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();

    }

    // ????????? ?????? ?????? ????????? ??????????????? ??????
    public void toggleFab() {
        if(fabMain_status) {
            // ????????? ?????? ?????? ??????
            // ??????????????? ??????
            ObjectAnimator fc_animation = ObjectAnimator.ofFloat(fabCamera, "translationY", 0f);
            fc_animation.start();
            ObjectAnimator fe_animation = ObjectAnimator.ofFloat(fabEdit, "translationY", 0f);
            fe_animation.start();
            // ?????? ????????? ????????? ??????
            fabMain.setImageResource(R.drawable.ic_baseline_add_24);

        }else {
            // ????????? ?????? ?????? ??????
            ObjectAnimator fc_animation = ObjectAnimator.ofFloat(fabCamera, "translationY", -300f);
            fc_animation.start();
            ObjectAnimator fe_animation = ObjectAnimator.ofFloat(fabEdit, "translationY", -600f);
            fe_animation.start();
            // ?????? ????????? ????????? ??????
            fabMain.setImageResource(R.drawable.ic_baseline_clear_24);
        }
        // ????????? ?????? ?????? ??????
        fabMain_status = !fabMain_status;
    }

    @Override
    public void onTextClicked(int position){
        Toast.makeText(getApplicationContext(), "sdsdsdsd"+position, Toast.LENGTH_LONG).show();

        Intent it = new Intent(MainActivity.this, CommShow.class);

        it.putExtra("comm_title", ArrTitle[position]);
        it.putExtra("comm_content", ArrCtx[position]);
        it.putExtra("comm_id", ArrId[position]);

        startActivity(it);

    }

    @Override
    public void onItemClicked(int position) {
        Toast.makeText(getApplicationContext(), "Item : "+position, Toast.LENGTH_SHORT).show();
        Intent it = new Intent(MainActivity.this, ContentActivity.class);

        it.putExtra("user_id", idArr[position]);


        it.putExtra("user_address", user_address);
        ItemData item = dataList.get(position);

        it.putExtra("content", contextArr[position]);
        it.putExtra("title", item.title);
        it.putExtra("money", item.money);
        // test 4??? ?????????

        it.putExtra("total_read_num", total_read_numArr[position]);
        it.putExtra("title", titleArr[position]);
        it.putExtra("address", addArr[position]);
        it.putExtra("money", moneyArr[position]);
        it.putExtra("typeOfContext", typeOfContextArr[position]);
        it.putExtra("negotiation", negoArr[position]);
        it.putExtra("currentStat", currentStatArr[position]);


        //?????? 11-30
        it.putExtra("my_id", user_id);


        startActivity(it);
    }

    public void onTitleClicked(int position) {
        Toast.makeText(getApplicationContext(), "Title : "+position, Toast.LENGTH_SHORT).show();
    }

    public void onContentClicked(int position) {
        Toast.makeText(getApplicationContext(), "Content : "+position, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onImageViewClicked(int position) {
        Toast.makeText(getApplicationContext(), "Image : "+position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMoneyClicked(int position) {
        Toast.makeText(getApplicationContext(), "Image : "+position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddressClicked(int position) {
        Toast.makeText(getApplicationContext(), "Image : "+position, Toast.LENGTH_SHORT).show();
    }

    public void onItemLongClicked(int position) {
        adapter.remove(position);
        Toast.makeText(getApplicationContext(),
                dataList.get(position).getTitle()+" is removed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        String str = "??????????????? ?????? ????????? 573-10";
        List list = null;
        double lat = 35.8914645, lon = 128.6146076;
/*
        try {
            list = geocoder.getFromLocationName(str,10);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list != null) {
            String city = "";
            String country = "";
            if (list.size() == 0) {

            }
            else {
                Address address = (Address)list.get(0);
                lat = address.getLatitude();
                lon = address.getLongitude();
            }
        }*/
        String address = getCurrentAddress(35.8909202, 128.6139879);



        mMap = googleMap;

        /*LatLng CHUMSEONG = new LatLng(lat, lon);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(CHUMSEONG);
        markerOptions.title("??????");
        markerOptions.snippet("????????? ??????");
        mMap.addMarker(markerOptions);*/

        String visit3[] = new String[10000000];
        DatabaseReference mFirebaseRef3 = database.getReference("users");
        mFirebaseRef3.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String s) {
                int idx = 0;
                for (DataSnapshot poly : snapshot.getChildren()) {

                    final Geocoder geocoder2 = new Geocoder(MainActivity.this, Locale.getDefault());

                    String ididid = snapshot.child("id").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);
                    String sex = snapshot.child("sex").getValue(String.class);
                    int age = Integer.parseInt(snapshot.child("age").getValue(String.class));
                    int contFlag = 0;
                    for(int i=0;i<idx;i++){
                        if(visit3[i].equals(email)){
                            contFlag = 1;
                            break;
                        }
                    }
                    if(contFlag == 1)
                        continue;
                    visit3[idx] = email;
                    idx++;

                    double lat2 = 35.8914645, lon2 = 128.6146076;

                    List lst1 = null;

                    try {
                        lst1 = geocoder2.getFromLocationName(address,10);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (lst1 != null) {
                        String city = "";
                        String country = "";
                        if (lst1.size() == 0) {

                        }
                        else {
                            Address address2 = (Address)lst1.get(0);
                            lat2 = address2.getLatitude();
                            lon2 = address2.getLongitude();
                        }
                    }
                    LatLng LL = new LatLng(lat2, lon2);

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(LL);
                    markerOptions.title(ididid);
                    markerOptions.snippet("sex : " + sex + ", age : " + age);

                    BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.policedog);
                    Bitmap b=bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, 120, 120, false);
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

                    mMap.addMarker(markerOptions);

                }

            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String s) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

/*
        double x1 = 35.88866614, x2 = 128.6121060;
        LatLng IllChungDam = new LatLng(x1, x2);
        markerOptions.position(IllChungDam);
        markerOptions.title("?????????");
        markerOptions.snippet("????????? ??????");
        mMap.addMarker(markerOptions);
*/


        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker arg0) {
                Intent it = new Intent(MainActivity.this, ContentActivity.class);

                String s = arg0.getTitle();

                int flag= 0;

                for(int i=commArrIdx - 1;i >= 0;i--) {
                    if (ArrId[i].equals(s)) {
                        //?????? test
                        it.putExtra("user_id", ArrId[i]);

                        it.putExtra("user_address", user_address);
                        ItemData item = dataList.get(i);

                       // it.putExtra("image", BitmapFactory.decodeResource(getResources(), cat[i]));
/*
                        Bitmap icon = BitmapFactory.decodeResource(getResources(), cat[i]);
//        it.putExtra("image", icon);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        icon.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        it.putExtra("image",byteArray);*/

                        it.putExtra("content", contextArr[i]);
                        it.putExtra("title", item.title);
                        it.putExtra("money", item.money);
                        // test 4??? ?????????

                        it.putExtra("total_read_num", total_read_numArr[i]);
                        it.putExtra("title", titleArr[i]);
                        it.putExtra("address", addArr[i]);
                        it.putExtra("money", moneyArr[i]);
                        it.putExtra("typeOfContext", typeOfContextArr[i]);
                        it.putExtra("negotiation", negoArr[i]);
                        it.putExtra("currentStat", currentStatArr[i]);

                        //?????? 11-30
                        it.putExtra("my_id", user_id);


                        startActivity(it);
                        flag = 1;
                        break;
                    }

                }
                if (flag == 0) {
                    Toast.makeText(getApplicationContext(), "????????? ??????", Toast.LENGTH_LONG).show();

                }
                Log.d("mGoogleMap1", "Activity_Calling");
            }
        });

/*        // ?????? ????????? ?????? ????????? ??????.
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                String M = marker.getSnippet();
                Intent it = new Intent(getApplicationContext(), ContentActivity.class);
                startActivity(it);
                return false;
            }
        });*/

        LatLng CHUMSEONG = new LatLng(lat, lon);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CHUMSEONG, 15));

    }

    // ??? ????????? ??????????????? ????????? ????????? ?????? //
    public String getCurrentAddress( double latitude, double longitude) {

        //????????????... GPS??? ????????? ??????
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //???????????? ??????
            Toast.makeText(this, "???????????? ????????? ????????????", Toast.LENGTH_LONG).show();
            return "???????????? ????????? ????????????";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "????????? GPS ??????", Toast.LENGTH_LONG).show();
            return "????????? GPS ??????";

        }



        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "?????? ?????????", Toast.LENGTH_LONG).show();
            return "?????? ?????????";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";

    }

    //????????? ????????? ?????????
    public void createNotificationChannel()
    {
        //notification manager ??????
        mNotificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        // ??????(device)??? SDK ?????? ?????? ( SDK 26 ?????? ???????????? - VERSION_CODES.O = 26)
        if(android.os.Build.VERSION.SDK_INT
                >= android.os.Build.VERSION_CODES.O){
            //Channel ?????? ?????????( construct ?????? )
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID
                    ,"Test Notification",mNotificationManager.IMPORTANCE_HIGH);
            //Channel??? ?????? ?????? ??????
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            // Manager??? ???????????? Channel ??????
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

    }

    // Notification Builder??? ????????? ?????????
    private NotificationCompat.Builder getNotificationBuilder(String user1, String user2) {
        Intent notificationIntent = new Intent(this, ChatActivity.class);
        notificationIntent.putExtra("my_id", user1);
        notificationIntent.putExtra("email", user2);

        PendingIntent notificationPendingIntent = PendingIntent.getActivity
                (this, 0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);





        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle(user1 + " <-------> " + user2)
                .setContentText("Someone Send you message")
                .setSmallIcon(R.drawable.policedog)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true);
        return notifyBuilder;
    }

    // Notification??? ????????? ?????????
    public void sendNotification(String user1, String user2){
        // Builder ??????
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder(user1, user2);
        // Manager??? ?????? notification ??????????????? ??????
        mNotificationManager.notify(0,notifyBuilder.build());
    }

}