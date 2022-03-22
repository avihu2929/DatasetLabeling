package com.example.datasetlabeling

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.File


class MainActivity : AppCompatActivity() {

    var flag = true
    lateinit var myImage: ImageView
    lateinit var labels: Array<String>
    lateinit var images: Array<String>
    lateinit var dialog: Dialog
    var imageCount = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myImage = findViewById<View>(R.id.imageViewLabel) as ImageView

        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/dataset/"
        val fileName = "data.txt"
        val file = File(path, fileName)
        //file.appendText("")
        if (!file.exists()){
            Toast.makeText(this,"exists",Toast.LENGTH_SHORT).show()
            file.appendText("id , class")
        }
        val fileName2 = "labels.txt"
        val file2 = File(path, fileName2)
        //file.appendText("")
        if (!file2.exists()){
            file2.appendText("")
        }
        var btn = findViewById<View>(R.id.button) as Button
        btn.setOnClickListener(View.OnClickListener {
            //showLabelDialog(labels)
            showVerifyDialog(file2)
        })
        loadLabels(file2)
        showVerifyDialog(file2)
        loadImages()
        loadNextImageFromFile(file)
        //showVerifyDialog(file2)
        nextImage()
       showLabelDialog(labels,file)
        myImage.setOnClickListener(View.OnClickListener {
            //showLabelDialog(labels)
            if (flag){
                showLabelDialog(labels,file)
                flag = false
            }

            dialog.show()
            // dialog.show()
        })

    }

    private fun loadNextImageFromFile(file: File){
        var ns = ""
        var s=file.readLines().last()
        var i = 0
        while(s[i]!=','){
            ns+=s[i]
            i++
        }
        if(ns!="id "){
            while (ns!=images[imageCount+1]){
                imageCount++
            }
            imageCount++
        }


    }

    private fun nextImage(){
        imageCount++
        val photoPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/dataset/0/"+images[imageCount]
        Toast.makeText(this,images[imageCount],Toast.LENGTH_LONG).show()
        val options = BitmapFactory.Options()
        Log.d("Photo Path",photoPath)
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        val bitmap = BitmapFactory.decodeFile(photoPath, options)
        myImage.setImageBitmap(bitmap)

    }


    private fun showVerifyDialog(file:File) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_verify_labels)


        val et = dialog.findViewById(R.id.editText) as EditText
        val btnDsms = dialog.findViewById(R.id.buttonVerifyDismiss) as Button


        for(line in file.readLines()){
            var text = et.text.toString() + line + "\n"
            et.setText(""+text)
        }
        btnDsms.setOnClickListener() {

            file.writeText(et.text.toString())
            loadLabels(file)
            dialog.dismiss()
            flag=true
            //  showLabelDialog(labels)

        }
        dialog.show()


    }

    private fun loadImages(){
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString() + "/dataset/0/"
        Log.d("Files", "Path: $path")
        val directory = File(path)
        val files = directory.listFiles()

        if (files != null) {
            images = Array<String>(files.size) { "" }
            for (i in files.indices) {
                 // Log.d("Files", "FileName:" + files[i].name)
                images[i] = files[i].name

            }

        }



        var x = 0
    }

    private fun loadLabels(file:File){
       /* val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString() + "/dataset/"
        Log.d("Files", "Path: $path")
        val directory = File(path)
        val files = directory.listFiles()

        if (files != null) {
            labels = Array<String>(files.size-2) { "" }
            var i =0
            var j = 0
            while(i < files.size){
                if(files[i].name!="0"&&files[i].name!="data.txt"){
                    labels[j] = files[i].name
                    j++
                }
                i++
            }
            showVerifyDialog(labels)
        }*/
        var count = 0
        for(line in file.readLines()){
            count++
        }
         labels = Array<String>(count){""}

        var i = 0
        for(line in file.readLines()){
            labels[i] = line
            i++
        }


    }

    private fun showLabelDialog(labels: Array<String>,txt:File) {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_label_image)
        val lv = dialog.findViewById(R.id.listViewLabels) as ListView
        val arrayAdapter: ArrayAdapter<*>
        for (i in labels.indices) {
            labels[i]="0      "+labels[i]
        }
        arrayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, labels
        )
        lv.adapter = arrayAdapter
        lv.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val selectedItemText = parent.getItemAtPosition(position)
            Toast.makeText(this,  selectedItemText as String, Toast.LENGTH_SHORT).show()
            if (selectedItemText[0] =='0'){
                selectedItemText[0] =='1'
                labels[position]="1"+labels[position].substring(1)
                arrayAdapter.notifyDataSetChanged()
            }else if(selectedItemText[0] =='1'){
                selectedItemText[0] =='0'
                labels[position]="0"+labels[position].substring(1)
                arrayAdapter.notifyDataSetChanged()
            }

        }
        dialog.setOnCancelListener(DialogInterface.OnCancelListener {
            dialog.hide()
        })
        val btnDone = dialog.findViewById(R.id.buttonDone) as Button
        btnDone.setOnClickListener(){
            //write images to folders
            //repeat for next image
            var i = 0
            Log.d("array count", arrayAdapter.count.toString())
            var path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/dataset/"
            var f = File(path+"0/"+images[imageCount])
            while(i<arrayAdapter.count){
                Log.d("item "+i, arrayAdapter.getItem(i).toString())
                var item = arrayAdapter.getItem(i).toString()
                if(item[0]=='1'){
                    //copy image to label's folder
                    //f.copyTo(File(path+item.substring(7)+"/"+images[imageCount]),true)
                    txt.appendText("\n"+images[imageCount]+", "+item.substring(7))
                }
                i++
            }
            for(i in labels.indices){
                if (labels[i][0]=='1'){
                    labels[i]="0"+labels[i].substring(1)
                }
            }
            arrayAdapter.notifyDataSetChanged()
            nextImage()
            // dialog.dismiss()
            dialog.hide()
        }

        //dialog.show()


    }

}