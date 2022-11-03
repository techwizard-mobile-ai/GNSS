package app.mvp.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.RemoteException
import android.util.Base64
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.activities.LoginActivity
import app.vsptracker.apis.login.LoginAPI
import app.vsptracker.apis.trip.MyData
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_mvp_home.*
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress


class MvpHomeActivity : BaseActivity(), View.OnClickListener {
  private val tag = this::class.java.simpleName
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_mvp_home, contentFrameLayout)
    val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
    navigationView.menu.getItem(0).isChecked = true
    myHelper.setTag(tag)
    
    Glide.with(this@MvpHomeActivity).load(ContextCompat.getDrawable(this@MvpHomeActivity, R.drawable.mvp_home_logo)).into(mvp_main_taputapu)
    Glide.with(this@MvpHomeActivity).load(ContextCompat.getDrawable(this@MvpHomeActivity, R.drawable.hub_logo_complete)).into(mvp_main_portal)
    
    mvp_main_taputapu.setOnClickListener(this@MvpHomeActivity)
    mvp_main_portal.setOnClickListener(this@MvpHomeActivity)
    mvp_main_logout.setOnClickListener(this@MvpHomeActivity)
  }
  
  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.mvp_main_taputapu -> {
        when {
          !myHelper.isOnline() -> myHelper.showErrorDialog(getString(R.string.no_internet_connection), getString(R.string.no_internet_explanation))
          else -> {
            val intent = Intent(this, MvpOrgsProjectsActivity::class.java)
            intent.putExtra("myData", MyData())
            startActivity(intent)
          }
        }
      }
      R.id.mvp_main_portal -> {
        val url = getString(R.string.portal_url)
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
      }
      R.id.mvp_main_logout -> {
        myHelper.setLoginAPI(LoginAPI())
        myHelper.setIsMapOpened(false)
        myHelper.setOperatorAPI(MyData())
        myHelper.setLastJourney(MyData())
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
      }
    }
  }
  
  fun run() {
    try {
      myHelper.log("inside run")
      val nUsername = "Dariusz"
      val nPassword = "Dariusz"
      val nServer = InetSocketAddress("www.smartfix.co.nz", 4109)
      val nPort = 0
      val nProtocol = "ntripv1"
      val nMountpoint = "GSAAsingleRTCM"
      val sockaddr: SocketAddress = nServer
      val nsocket = Socket()
      nsocket.connect(sockaddr, 10 * 1000)
      myHelper.log("socket")
      myHelper.log(nsocket.toString())
      if (nsocket.isConnected()) {
        nsocket.setSoTimeout(20 * 1000)
        val nis = nsocket.getInputStream()
        val nos = nsocket.getOutputStream()
        if (nProtocol.equals("ntripv1")) {
          var requestmsg = """GET /${nMountpoint.toString()} HTTP/1.0
"""
//                    val data: ByteArray =":"
//                    val base64 = Base64.encodeToString(data, Base64.DEFAULT)
          val data1 = Base64.decode("Dariusz:Dariusz", Base64.DEFAULT).toString(charset("UTF-8"))
          
          requestmsg += "User-Agent: NTRIP LefebureNTRIPClient/20131124\r\n"
          requestmsg += """
                    Accept: */*
                    Connection: close
                    
                    """.trimIndent()
          if (nUsername.length > 0) {
            requestmsg += """
                        Authorization: Basic ${data1}
                        
                        """.trimIndent()
          }
          requestmsg += "\r\n"
          nos.write(requestmsg.toByteArray())
        } else {
        }
        val buffer = ByteArray(4096)
        var read: Int = nis.read(buffer, 0, 4096)
        while (read != -1) {
          val tempdata = ByteArray(read)
          System.arraycopy(buffer, 0, tempdata, 0, read)
          try {
//                        dataMessenger.send(Message.obtain(null, MSG_NETWORK_GOT_DATA, tempdata))
          }
          catch (e2: RemoteException) {
          }
          read = nis.read(buffer, 0, 4096)
        }
      }
    }
    catch (e: Exception) {
//            LogMessage(e.localizedMessage)
      e.printStackTrace()
      myHelper.log("Exception: " + e.localizedMessage)
    }
  }
}