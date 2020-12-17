

# middle_experiment

期中作业：NotePad

**一、基本功能实现**

1. 显示时间戳：

   NotePad的数据表中含有“修改时间”一项，使用它作为时间戳，显示在note标题下方。可以在NotePad.java契约类中找到。

   ```java
   /**
    * Column name for the modification timestamp
    * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
    */
   public static final String COLUMN_NAME_MODIFICATION_DATE = "modified";
   ```

   ①修改notelist_item.xml文件，新增一条TextView，用于显示时间戳。

   ![image](https://github.com/vency799/mid-trem-test/blob/master/notelist_item_lay.jpg)

   ②打开NoteList.java，在 PROJECTION 中加入修改时间条目 NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE，应用显示笔记时，获取 PROJECTION 中设定的值。

   ![image](https://github.com/vency799/middle_experiment/blob/master/projection.jpg)

   在下方的 dataColumns 中添加 时间戳 条目、在 viewIDs 中添加 notelist_item 中新增 TextView 的 id

   ![image](https://github.com/vency799/middle_experiment/blob/master/viewid.jpg)

   ③修改时间戳的格式，原格式是 Long 型数据：System.currentTimeMillis()。

   在 NoteEditor.java 中的updateNote() 中设置，获取当前时间

   ```java
   Long nowtime = System.currentTimeMillis();
   Date d = new Date(nowtime);
   ```

   使用 SimpleDateFormat 修改格式，然后将新格式的值放到Maps中

   ```java
   SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
   String format = sf.format(d);
   values.put(NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE, format);
   ```

   设置完后，每次修改 note ，点击保存都会将当前时间作为修改时间，存入到数据库中并在 NoteList 中显示

   ![image](https://github.com/vency799/mid-trem-test/blob/master/title_time.jpg)

2. 新增查询功能：

   实现查询功能需要新建一个 Activity，对 notes 的 title 与 note 进行搜索。

   ①新建布局文件 note_search.xml ，对 Activity 进行布局

   ```java
   <SearchView
           android:id="@+id/search_view"
           android:layout_width="match_parent"
           android:layout_height="wrap_content"
           android:iconifiedByDefault="false">
       </SearchView>
       <ListView
           android:id="@+id/list_view"
           android:layout_width="match_parent"
           android:layout_height="match_parent" />
   ```

   ②新建 NoteSearch.java，继承 Activity ，实现 SearchView.OnQueryTextListener接口，实现思路与 NoteList 显示内容相似

   PROJECTION 中存放需查询的数据

   ![image](https://github.com/vency799/mid-trem-test/blob/master/note_search_1.jpg)

   复写 onCreate、onQueryTextChange 两个方法

   ```java
   @Override
   protected void onCreate(Bundle savedInstanceState){
       super.onCreate(savedInstanceState);
       //layout of search activity
       setContentView(R.layout.note_search);
       //get the search_key
       SearchView searchView = findViewById(R.id.search_view);
       Intent intent = getIntent();
   
       if(intent.getData() == null){
           intent.setData(NotePad.Notes.CONTENT_URI);
       }
       //show the search result
       listView = findViewById(R.id.list_view);
       sqLiteDatabase = new NotePadProvider.DatabaseHelper(this).getReadableDatabase();
   
       //search button
       searchView.setSubmitButtonEnabled(true);
       //search default text
       searchView.setQueryHint("查找");
       searchView.setOnQueryTextListener(this);
   }
   @Override
   //show the search result dynamically(like ajax)
   public boolean onQueryTextChange(String string){
       //search notes by title or note
       String selection = NotePad.Notes.COLUMN_NAME_TITLE+" like ? or "+ NotePad.Notes.COLUMN_NAME_NOTE+" like ?";
       //set the data into "?"
       String[] selectionargs = {"%"+string+"%","%"+string+"%"};
   
       //ResultSet Cursor
       //get data
       Cursor cursor = sqLiteDatabase.query(
               NotePad.Notes.TABLE_NAME,
               PROJECTION,
               selection,
               selectionargs,
               null,
               null,
               NotePad.Notes.DEFAULT_SORT_ORDER
       );
   
   
       //display
       String[] dataColumns = {
               NotePad.Notes.COLUMN_NAME_TITLE,
               NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE
       };
       //ID
       int[] viewIDs = {
               android.R.id.text1,
               android.R.id.text2
       };
       //show the results
       SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(
               this,
               R.layout.noteslist_item,
               cursor,
               dataColumns,
               viewIDs
       );
       listView.setAdapter(simpleCursorAdapter);
       return true;
   }
   ```

   ③在 list_options_menu.xml 中添加 Search 菜单项，设置 id：menu_search；title：Search

   ```java
   <item android:id="@+id/menu_search"
           android:title="@string/menu_search"
           android:showAsAction="always"
           />
   ```

   *(如果需要插入图片，可将图标存入 drawable 中，使用 android:icon 调用)*

   ④回到 NoteList 中，找到 onOptionsItemSelected 方法，新增 case 条件，调用刚才设置的 id，跳转到 NoteSearch Activity执行

   ```java
   @Override
       public boolean onOptionsItemSelected(MenuItem item) {
           switch (item.getItemId()) {
           ...
   
           case R.id.menu_search:
               Intent intent = new Intent(this, NoteSearch.class);
               this.startActivity(intent);
               return true;
           default:
               return super.onOptionsItemSelected(item);
           }
       }
   ```

   ⑤在 AndroidManifest 中为 NoteSearch 注册

   ```java
   <application>
   	...
   	<activity android:name=".NoteSearch" android:label="@string/search_note"/>
   </application>
   ```

   设置好后，NoteList 界面菜单栏会显示 SEARCH 选项，点击进行搜索

   ![image](https://github.com/vency799/mid-trem-test/blob/master/note_search_2.jpg)

   ![image](https://github.com/vency799/mid-trem-test/blob/master/note_search_3.jpg)

   ![image](https://github.com/vency799/mid-trem-test/blob/master/note_search_4.jpg)

**二、其他功能实现**

1. 修改文本背景颜色、字体颜色

   在 文本编辑 界面的菜单中添加 修改背景颜色 与 修改字体颜色 两个选项，首先需要一个菜单布局 note_changecolor.xml。

   用 Button 作为颜色选择，根据需要添加 Button，采用 水平 布局。

   ```java
   <?xml version="1.0" encoding="utf-8"?> 
   <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
       android:orientation="horizontal" android:layout_width="match_parent"
       android:layout_height="match_parent">
   
       <Button
           android:id="@+id/red"
           android:layout_width="wrap_content"
           android:layout_height="wrap_content"
           android:background="#C11313"
           android:layout_weight="1"
           android:onClick="onClick"
           />
       <Button
           android:id="@+id/green"
           android:layout_width="wrap_content"
           android:layout_height="wrap_content"
           android:background="#2DA32D"
           android:layout_weight="1"
           android:onClick="onClick"
           />
       ...
   </LinearLayout>
   ```

   ![image](https://github.com/vency799/mid-trem-test/blob/master/note_changec_2.jpg)

   然后在 editor_options_menu.xml 中添加菜单项：一级菜单 改变颜色，二级菜单 改变背景颜色 与 改变字体颜色。

   ```java
   <?xml version="1.0" encoding="utf-8"?>
   <menu xmlns:android="http://schemas.android.com/apk/res/android">
       ...
       <item android:title="@string/changecolor">
           <menu>
               <item android:title="@string/changebackcolor"
                   android:id="@+id/backgroundcolor"></item>
               <item android:title="@string/changetextcolor"
                   android:id="@+id/textcolor"></item>
           </menu>
       </item>
       ...
   </menu>
   ```

   ![image](https://github.com/vency799/mid-trem-test/blob/master/note_changec_1.jpg)

   在 NoteEditor.java 中的 onOptionsItemSelected() 设置菜单的响应操作，case id 为上面子菜单的 id。

   修改颜色使用同一个方法 change_color()，引入一个 boolean 变量 Flag 作为修改背景颜色或修改字体颜色。

   ```java
   @Override
       public boolean onOptionsItemSelected(MenuItem item) {
           // Handle all of the possible menu actions.
           switch (item.getItemId()) {
           case R.id.menu_save:
               ...
           case R.id.backgroundcolor:
               Flag = true;
               change_color();
               break;
           case R.id.textcolor:
               Flag = false;
               change_color();
               break;
          	...
           }
           return super.onOptionsItemSelected(item);
       }
   ```

   在 NoteEditor.java 中实现改变颜色的方法 change_color() ，使用 AlterDialog 对话框进行操作，.setView() 设置为刚才的布局文件。

   ```java
   @TargetApi(Build.VERSION_CODES.LOLLIPOP)
       private void change_color(){
           AlertDialog alertDialog = new AlertDialog.Builder(this)
                   .setTitle("请选择颜色")
                   .setView(R.layout.note_changecolor)
                   .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           dialog.dismiss();
                       }
                   }).create();
           alertDialog.show();
       }
   ```

   note_changecolor.xml 中每个 Button 都调用 onClick() 方法实现修改颜色，所以在 change_color() 下面实现 onClick()方法。

   *（有多少个 Button 就设置多少个 case）*

   backcolor 与 textcolor 作为更新数据库的内容，将修改后的颜色的16进制数存入数据库中。

   ```java
   public void onClick(View v){
           switch (v.getId()){
               case R.id.red:
                   if (Flag) {
                       mText.setBackgroundColor(Color.parseColor("#C11313"));
                       backcolor = "#C11313";
                   } else {
                       mText.setTextColor(Color.parseColor("#C11313"));
                       textcolor = "#C11313";
                   }
                   break;
               case R.id.green:
                   if (Flag) {
                       mText.setBackgroundColor(Color.parseColor("#2DA32D"));
                       backcolor = "#2DA32D";
                   } else {
                       mText.setTextColor(Color.parseColor("#2DA32D"));
                       textcolor = "#2DA32D";
                   }
                   break;
               case R.id.blue:
                   if (Flag) {
                       mText.setBackgroundColor(Color.parseColor("#1C1CB3"));
                       backcolor = "#1C1CB3";
                   } else {
                       mText.setTextColor(Color.parseColor("#1C1CB3"));
                       textcolor = "#1C1CB3";
                   }
                   break;
               ...
           }
   ```

   至此已实现修改文本背景颜色与字体颜色，剩下将修改的颜色保存到数据库的步骤。

   ①契约类中添加两个字段

   ```java
    /**
     * column name for the background color
     */
    public static final String COLUMN_NAME_BACKGROUND_COLOR = "bcolor";
   
    /**
     * column name for the text background color
     */
    public static final String COLUMN_NAME_TEXT_BACKGROUND_COLOR = "tcolor";
   ```

   ②NoteProvider Maps绑定新增的两个字段，修改建库语句

   ```java
   //Maps "background color" to "bcolor"
   sNotesProjectionMap.put(
           NotePad.Notes.COLUMN_NAME_BACKGROUND_COLOR,
           NotePad.Notes.COLUMN_NAME_BACKGROUND_COLOR);
   
   //Maps "text background color" to "tcolor"
   sNotesProjectionMap.put(
           NotePad.Notes.COLUMN_NAME_TEXT_BACKGROUND_COLOR,
           NotePad.Notes.COLUMN_NAME_TEXT_BACKGROUND_COLOR);
   ...
   @Override
          public void onCreate(SQLiteDatabase db) {
              db.execSQL("CREATE TABLE " + NotePad.Notes.TABLE_NAME + " ("
                      + NotePad.Notes._ID + " INTEGER PRIMARY KEY,"
                      + NotePad.Notes.COLUMN_NAME_TITLE + " TEXT,"
                      + NotePad.Notes.COLUMN_NAME_NOTE + " TEXT,"
                      + NotePad.Notes.COLUMN_NAME_CREATE_DATE + " INTEGER,"
                      + NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE + " INTEGER"
                      + NotePad.Notes.COLUMN_NAME_BACKGROUND_COLOR + "TEXT"
                      + NotePad.Notes.COLUMN_NAME_TEXT_BACKGROUND_COLOR + "TEXT"
                      + ");");
          }
   ```

   ③回到 NoteEditor.java 中，PROJECTION 需要新增两个字段；实现修改后存入 与 读取修改后的颜色 两部分功能

   ```java
   private static final String[] PROJECTION =
           new String[] {
               NotePad.Notes._ID,
               NotePad.Notes.COLUMN_NAME_TITLE,
               NotePad.Notes.COLUMN_NAME_NOTE,
               NotePad.Notes.COLUMN_NAME_BACKGROUND_COLOR,
               NotePad.Notes.COLUMN_NAME_TEXT_BACKGROUND_COLOR,
       };
   ```

   修改后存入：

   ```java
   private final void updateNote(String text, String title) {
           // Sets up a map to contain values to be updated in the provider.
           ...
           //This puts the bcolor into the map.
           values.put(NotePad.Notes.COLUMN_NAME_BACKGROUND_COLOR, backcolor);
   
           //This puts the tcolor into the map.
           values.put(NotePad.Notes.COLUMN_NAME_TEXT_BACKGROUND_COLOR, textcolor);
       	...
           getContentResolver().update(
                   mUri,    // The URI for the record to update.
                   values,  // The map of column names and new values to apply to them.
                   null,    // No selection criteria are used, so no where columns are necessary.
                   null     // No where columns are used, so no where arguments are necessary.
               );
       }
   ```

   读取修改后的：

   ```java
   @Override
       protected void onResume() {
           super.onResume();
   		   ...
                  
               int colNoteIndex = mCursor.getColumnIndex(NotePad.Notes.COLUMN_NAME_NOTE);
               String note = mCursor.getString(colNoteIndex);
               mText.setTextKeepState(note);
   
               int back_color = mCursor.getColumnIndex(NotePad.Notes.COLUMN_NAME_BACKGROUND_COLOR);
               String b_color = mCursor.getString(back_color);
               if(b_color != null){
                   mText.setBackgroundColor(Color.parseColor(b_color));
               }else {
                   mText.setBackgroundColor(Color.parseColor("#FFFFFF"));
               }
   
   
               int text_color = mCursor.getColumnIndex(NotePad.Notes.COLUMN_NAME_TEXT_BACKGROUND_COLOR);
               String t_color = mCursor.getString(text_color);
               if(t_color != null){
                   mText.setTextColor(Color.parseColor(t_color));
               }else {
                   mText.setTextColor(Color.parseColor("#000000"));
               }
               
               // Stores the original note text, to allow the user to revert changes.
               if (mOriginalContent == null) {
                   mOriginalContent = note;
               }
   
           /*
            * Something is wrong. The Cursor should always contain data. Report an error in the
            * note.
            */
           } else {
               setTitle(getText(R.string.error_title));
               mText.setText(getText(R.string.error_message));
           }
       }
   ```

   实现界面：

   ![image](https://github.com/vency799/mid-trem-test/blob/master/note_changec_3.jpg)

   ![image](https://github.com/vency799/mid-trem-test/blob/master/note_changec_4.jpg)

   ![image](https://github.com/vency799/mid-trem-test/blob/master/note_changec_5.jpg)

2. 新增提醒功能

   在 文本编辑 界面的菜单中添加 设置提醒时间 选项，修改 note_editor.xml 布局文件，新增一个 按钮 控件。

   原代码使用的是 View 界面，在 NoteEditor.java 中继承 EditText；

   新布局使用 线性布局、垂直分布，上方按钮，下方 EditText 直接使用，无影响。

   ![image](https://github.com/vency799/mid-trem-test/blob/master/note_alert_1.jpg)

   然后在 editor_options_menu.xml 中添加菜单项：一级菜单 设置提醒时间，二级菜单 选择日期 与 选择时间。

   ```java
   <?xml version="1.0" encoding="utf-8"?>
   <menu xmlns:android="http://schemas.android.com/apk/res/android">
       ...
       <item android:title="@string/setalert">
           <menu>
               <item android:title="@string/choicedate"
                   android:id="@+id/set_date"></item>
               <item android:title="@string/choicetime"
                   android:id="@+id/set_time"></item>
           </menu>
       </item>
       ...
   </menu>
   ```

   ![image](https://github.com/vency799/mid-trem-test/blob/master/note_changec_1.jpg)

   在 NoteEditor.java 中的 onOptionsItemSelected() 设置菜单的响应操作，case id 为上面子菜单的 id。

   ```java
   @Override
       public boolean onOptionsItemSelected(MenuItem item) {
           // Handle all of the possible menu actions.
           switch (item.getItemId()) {
           ...
           case R.id.set_date:
               choiceDate();
               break;
           case R.id.set_time:
               choiceTime();
               break;
           }
           return super.onOptionsItemSelected(item);
       }
   ```

   在 NoteEditor.java 中实现 选择日期 choiceDate() 与 选择时间 choiceTime()。

   分别使用 DatePikerDialog 与 TimePickerDialog 对话框实现时间的选择。

   调用系统默认的日历与时钟，主题为 暗色 主题（THEME_HOLD_DARK).

   ```java
   /**
        *choice the alert date
        */
       public void choiceDate(){
   //        Toast toast = Toast.makeText(this, "date", Toast.LENGTH_SHORT);
   //        toast.show();
           //获取日历
           final Calendar calendar = Calendar.getInstance();
           final Button datebtn = findViewById(R.id.datebutton);
           //构建对话框
           DatePickerDialog dialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_DARK,
                   new DatePickerDialog.OnDateSetListener() {
                       @SuppressLint("SetTextI18n")
                       @Override
                       public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                           date = year + "-" + (month+1) + "-" + dayOfMonth;
                           if (time == null) {
                               datebtn.setText(date+time);
                           }else {
                               String text = calendar.get(Calendar.HOUR_OF_DAY) + ":" + (calendar.get(Calendar.MINUTE) + 3);
                               time = " " + text;
                               datebtn.setText(date+time);
                           }
                           datebtn.setVisibility(View.VISIBLE);
                       }
                   },
                   calendar.get(Calendar.YEAR),
                   calendar.get(Calendar.MONTH),
                   calendar.get(Calendar.DAY_OF_MONTH));
           dialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
           dialog.setTitle("选择日期：");
           dialog.show();
       }
   
       /**
        *choice the alert time
        */
       public void choiceTime(){
   //        Toast toast = Toast.makeText(this, "world", Toast.LENGTH_SHORT);
   //        toast.show();
           //获取日历
           final Calendar calendar = Calendar.getInstance();
           final Button datebtn = findViewById(R.id.datebutton);
           TimePickerDialog dialog = new TimePickerDialog(this, AlertDialog.THEME_HOLO_DARK,
                   new TimePickerDialog.OnTimeSetListener() {
                       @SuppressLint("SetTextI18n")
                       @Override
                       public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                           String text = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH)+1) + "-" +calendar.get(Calendar.DAY_OF_MONTH);
   
                           if(text.equals(date) || date == null){
                               if(hourOfDay <= calendar.get(Calendar.HOUR_OF_DAY)){
                                   if(minute - 3 <= calendar.get(Calendar.MINUTE)){
                                       time = " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + (calendar.get(Calendar.MINUTE) + 3);
                                   }else {
                                       time = " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + minute;
                                   }
                               }else {
                                   time = " " + hourOfDay + ":" + minute;
                               }
                           }else {
                               time = " " + hourOfDay + ":" + minute;
                           }
   
                           if (date == null) {
                               datebtn.setText(date+time);
                           }else {
                               date = text;
                               datebtn.setText(text+time);
                           }
   
                           datebtn.setVisibility(View.VISIBLE);
                       }
                   },
                   calendar.get(Calendar.HOUR_OF_DAY),
                   calendar.get(Calendar.MINUTE), true);
           dialog.setTitle("选择时间：");
           dialog.show();
       }
   ```

   如图：

   ![image](https://github.com/vency799/mid-trem-test/blob/master/note_alert_2.jpg)

   ![image](https://github.com/vency799/mid-trem-test/blob/master/note_alert_3.jpg)

   选择时间后，选择的时间将显示在按钮上：

   ![image](https://github.com/vency799/mid-trem-test/blob/master/note_alert_4.jpg)

   点击保存后，获取时间并通过 Boardcoast 广播到系统，到时便在系统通知中提醒

   实现方法 NoteEditor makeAlert():

   ```java
   /**
        * boardcoast date
        */
       @TargetApi(Build.VERSION_CODES.KITKAT)
       private void makeAlert() {
   //        Log.v(String.valueOf(NoteEditor.this),"yunxingqian");
           if (date != null && time != null) {
               SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
               long t = System.currentTimeMillis() + 5000;
               try {
                   t = Objects.requireNonNull(simpleDateFormat.parse(date + time)).getTime();
               } catch (Exception e) {
                   e.printStackTrace();
               }
   
               Intent intent = new Intent(NoteEditor.this, RemindAction.class);
   
               //文本存入intent
               //标题
               intent.putExtra("title", mCursor.getString(mCursor.getColumnIndex(NotePad.Notes.COLUMN_NAME_TITLE)));
               //内容
               intent.putExtra("note", mCursor.getString(mCursor.getColumnIndex(NotePad.Notes.COLUMN_NAME_NOTE)));
               //组装
               PendingIntent pi = PendingIntent.getBroadcast(
                       NoteEditor.this, mCursor.getInt(mCursor.getColumnIndex(NotePad.Notes._ID)),
                       intent,
                       PendingIntent.FLAG_UPDATE_CURRENT);
   
               //获取日历
               Calendar calendar = Calendar.getInstance();
               calendar.setTimeInMillis(System.currentTimeMillis());
               calendar.add(Calendar.SECOND, (int)((t-System.currentTimeMillis())/1000));
   
               //定时
               AlarmManager am= (AlarmManager)getSystemService(ALARM_SERVICE);
               am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
           }
       }
   
   ```

   新增接收类 RemindAction.java， 接收 NoteEditor 的 intent。

   ```java
   public class RemindAction extends BroadcastReceiver {
   
       public static int id = 0;
       @Override
       public void onReceive(Context context, Intent intent) {
           PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
   
           NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
   
           Notification.Builder mBuilder = new Notification.Builder(context);
           //标题
           mBuilder.setContentTitle(intent.getStringExtra("title"));
           //内容
           mBuilder.setContentText(intent.getStringExtra("note"));
           //小图标
           mBuilder.setSmallIcon(R.drawable.app_notes);
           //大图标
           mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app_notes));
   
           mBuilder.setContentIntent(pi);
           //点击自动消失
           mBuilder.setAutoCancel(true);
   
           //创建通知
           Notification notification = mBuilder.build();
           notificationManager.notify(id++, notification);
       }
   }
   ```

   最重要的一步：注册表

   ![image](https://github.com/vency799/mid-trem-test/blob/master/note_alert_8.jpg)

   实现效果：

   ![image](https://github.com/vency799/mid-trem-test/blob/master/note_alert_5.jpg)

   ![image](https://github.com/vency799/mid-trem-test/blob/master/note_alert_6.jpg)

   ![image](https://github.com/vency799/mid-trem-test/blob/master/note_alert_7.jpg)

   *小功能：取消提醒时间，点击 设置提醒时间 按钮即可取消提醒。

   ```javascript
       /**
        * 点击取消提醒时间
        */
       public void alertCancel(View v){
           final Button datebtn = findViewById(R.id.datebutton);
           AlertDialog.Builder builder = new AlertDialog.Builder(this);
   
           builder.setMessage("删除提醒时间？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   date = null;
                   time = null;
                   datebtn.setVisibility(View.GONE);
                   dialog.dismiss();
               }
           }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   dialog.dismiss();
               }
           });
           builder.show();
       }
   ```

   ![image](https://github.com/vency799/mid-trem-test/blob/master/note_cancelalert_1.jpg)

   ![image](https://github.com/vency799/mid-trem-test/blob/master/note_cancelalert_2.jpg)

   ![image](https://github.com/vency799/mid-trem-test/blob/master/note_cancelalert_3.jpg)

