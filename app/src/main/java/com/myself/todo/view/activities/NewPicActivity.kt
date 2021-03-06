package com.myself.todo.view.activities

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.mikhaellopez.rxanimation.fadeIn
import com.myself.todo.R
import com.myself.todo.Utils.Utilities
import com.myself.todo.databinding.ActivityNewPicBinding
import com.myself.todo.model.FotosDB
import com.myself.todo.model.beans.Album
import de.mateware.snacky.Snacky
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.android.synthetic.main.activity_new_pic.*

class NewPicActivity : AppCompatActivity(),PermissionListener {
    var url: String? = null
    val user = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actbind: ActivityNewPicBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_pic)
        setupview()
        requestPermissions()
        setContentView(actbind.root)
    }

    private fun setupview(){
        save.setOnClickListener { save() }
        albpic.setOnClickListener { startPicker() }
        diapic.text = Utilities.actualday()
        loadgif()

    }

    private fun startPicker() {
        val permmited = allpermmitted()
        if (permmited) {
            TedBottomPicker.with(this)
                    .show { uri ->
                        url = uri!!.path
                        loadpic()
                    }
        }

    }

    private fun requestPermissions(){
        TedPermission.with(this)
                .setPermissionListener(this)
                .setDeniedMessage("Se você não aceitar essa permissão não poderá adicionar fotos...\n\nPor favor ligue as permissões em [Configurações] > [Permissões]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check()
    }


    private fun allpermmitted(): Boolean {
        val write = TedPermission.isGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val read = TedPermission.isGranted(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val camera = TedPermission.isGranted(this, Manifest.permission.CAMERA)
        return write && read && camera
    }


    private fun loadpic(){
        Glide.with(this).load(url).into(albpic)
    }
    private fun loadgif(){
        Glide.with(this).asGif().load(Utilities.imagegif).into(albpic)
        albpic.fadeIn().andThen(diapic.fadeIn()).andThen(descricaopic.fadeIn()).andThen(save.fadeIn()).subscribe()
    }



    private fun createAlbum():Album{
        return Album(null,url,descricaopic.text.toString(),Utilities.actualday(),false,user?.uid)
    }

    private fun signIn() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            val providers = listOf(
                    AuthUI.IdpConfig.GoogleBuilder().build(),
                    AuthUI.IdpConfig.EmailBuilder().build())
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                    .setLogo(R.mipmap.ic_launcher)
                    .setAvailableProviders(providers)
                    .setTheme(R.style.AppTheme)
                    .build(), Utilities.RC_SIGN_IN)
        }
    }


    private fun save() {
        if (user == null){
            Snacky.builder().error().setText("Você precisa estar logado para salvar.").setAction("Login") {
                signIn()
            }.show()
            return
        }
        val album = createAlbum()

        if (album.fotouri.isNullOrBlank()) {
            Snacky.builder().setActivity(this).error().setText("Calma ai né, salvar o nada é meio impossível para nós ${Utilities.randomsadmoji()}")
        } else {
            if (album.description.isNullOrBlank()) {
                Snacky.builder().setActivity(this).warning()
                        .setText("Você está prestes a salvar uma foto sem legenda, deseja salvar assim mesmo?")
                        .setAction("Salvar") {
                            FotosDB(this).inserir(album)
                        }.show()
            } else {
                FotosDB(this).inserir(createAlbum())
            }
        }
    }







    override fun onPermissionGranted() {
        startPicker()
    }

    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
        Snacky.builder().setActivity(this).error().setText("Se não permitir o acesso não da para salvar as fotos...").show()
    }
}