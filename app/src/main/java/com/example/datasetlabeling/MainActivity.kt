package com.example.datasetlabeling

import android.app.Dialog
import android.content.DialogInterface
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
import java.nio.file.Path
import java.nio.file.Paths


class MainActivity : AppCompatActivity() {


    lateinit var myImage: ImageView
    lateinit var labels: Array<String>
    lateinit var images: Array<String>
    lateinit var dialog: Dialog
    var imageCount = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myImage = findViewById<View>(R.id.imageViewLabel) as ImageView
        myImage.setOnClickListener(View.OnClickListener {
            //showLabelDialog(labels)
            dialog.show()
        })
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/dataset/"
        val path2 = path+"0/009961_1.jpg"
        var fd = File(path2)
        fd.delete()
        fd.absoluteFile.delete()
        fd.canonicalFile.delete()

        val fileName = "data.txt"

        val file = File(path, fileName)
        file.appendText("")

        loadLabels()
        loadImages()
        nextImage()
        showLabelDialog(labels,file)
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


    private fun showVerifyDialog(labels: Array<String>) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_verify_labels)


        val tv = dialog.findViewById(R.id.textViewVerify) as TextView
        val btnDsms = dialog.findViewById(R.id.buttonVerifyDismiss) as Button

        Log.d("Files", "Size: " + labels.size)
        for (i in labels.indices) {
            Log.d("Files", "FileName:" + labels[i])

            tv.text = tv.text as String + "\n" + labels[i]
        }
        btnDsms.setOnClickListener() {


            dialog.hide()
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

    private fun loadLabels(){
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
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
                    f.copyTo(File(path+item.substring(7)+"/"+images[imageCount]),true)
                    txt.appendText("\n"+images[imageCount]+" , "+item.substring(7))
                }
                i++
            }

            nextImage()
            // dialog.dismiss()
            dialog.hide()
        }

        //dialog.show()


    }

}