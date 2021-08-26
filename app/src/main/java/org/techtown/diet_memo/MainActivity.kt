package org.techtown.diet_memo

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //firebase 연결
        val database = Firebase.database
        val myRef = database.getReference("myMemo")

        val listView = findViewById<ListView>(R.id.mainLV)
        val dataModelList = mutableListOf<DataModel>()

        //adapter에 값 전달하기
        val adapter_list = ListViewAdapter(dataModelList)

        //어댑터 연결
        listView.adapter = adapter_list
        Log.d("DataModel-------", dataModelList.toString())


        //데이터 읽기
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataModelList.clear()

                //dataSnap = [ {id:value, memo:value, date:value},
                // {id:value, memo:value, date:value}, ...]

                //데베에 값 읽어서 list에 저장
                for (dataModel in dataSnapshot.children) {
                    Log.d("DATA", dataModel.toString())
                    dataModelList.add(dataModel.getValue(DataModel::class.java)!!)

                }
                adapter_list.notifyDataSetChanged()
                Log.d("DataModel", dataModelList.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                //Log.w(TAG, "Failed to read value.", error.toException())
            }
        })



        //다이얼로그 띄우기
        val writeButton = findViewById<ImageView>(R.id.writeBtn)
        writeButton.setOnClickListener {
            //inflation
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
            //다이얼로그 연결
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("운동 메모 다이얼로그")

            val mAlertDialog = mBuilder.show()



            val DateSelectBtn= mAlertDialog.findViewById<Button>(R.id.dateSelectBtn)
            var dateText = ""

            //달력 띄워서 저장하기
            DateSelectBtn?.setOnClickListener {

                val today= GregorianCalendar()
                val year : Int = today.get(Calendar.YEAR)
                val month : Int = today.get(Calendar.MONTH)
                val date : Int = today.get(Calendar.DATE)
                //달력 띄우기
                val dlg= DatePickerDialog( this, object : DatePickerDialog.OnDateSetListener{
                    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                        //날짜 가져오기
                        Log.d("MAIN", "${year}. ${month+1}. ${dayOfMonth}")
                        //날짜를 버튼에 저장
                        DateSelectBtn.setText("${year}. ${month+1}. ${dayOfMonth}")

                        dateText = "${year}. ${month+1}. ${dayOfMonth}"
                    }

                },year,month,date)
                dlg.show()
            }


            //데베에 저장하기
            val saveBtn = mAlertDialog.findViewById<Button>(R.id.saveBtn)
            saveBtn?.setOnClickListener {

                val healthmemo = mAlertDialog.findViewById<EditText>(R.id.healthMemo)?.text.toString()

                val model= DataModel(dateText, healthmemo)

                val database = Firebase.database
                val myRef = database.getReference("myMemo")

                myRef.push().setValue(model)

                mAlertDialog.dismiss()

            }

        }
    }
}