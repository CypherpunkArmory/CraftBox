package tech.ula.craftbox

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import tech.ula.model.entities.App
import tech.ula.utils.defaultSharedPreferences

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val startButton = this.findViewById<Button>(R.id.startButton)
        startButton.setOnClickListener {
            saveState()
            startServer()
        }
        setupNgrokHyperlink()
        restoreState()
    }

    fun setupNgrokHyperlink() {
        val linkTextView = findViewById<TextView>(R.id.authTokenTextView)
        linkTextView.setMovementMethod(LinkMovementMethod.getInstance());
        linkTextView.setLinkTextColor(Color.WHITE)
    }

    fun findIndex(arr: Array<String>, item: String): Int? {
        return (arr.indices)
                .firstOrNull { i: Int -> item.equals(arr[i]) }
    }

    private fun restoreState() {
        val authTokenEditText = this.findViewById<EditText>(R.id.authTokenEditText)
        authTokenEditText.setText(defaultSharedPreferences.getString("authTokenEditText",""))

        val serverVersionSpinner = this.findViewById<Spinner>(R.id.serverVersionSpinner)
        val versionsArray = resources.getStringArray(R.array.server_versions)
        val versionPosition = findIndex(versionsArray, defaultSharedPreferences.getString("serverVersionSpinner","")!!)
        if (versionPosition != null)
            serverVersionSpinner.setSelection(versionPosition)

        val memorySpinner = this.findViewById<Spinner>(R.id.memorySpinner)
        val memoryArray = resources.getStringArray(R.array.memory)
        val memoryPosition = findIndex(memoryArray, defaultSharedPreferences.getString("memorySpinner","1024")!!)
        if (memoryPosition != null)
            memorySpinner.setSelection(memoryPosition)
    }

    private fun saveState() {
        val serverVersionSpinner = this.findViewById<Spinner>(R.id.serverVersionSpinner)
        val memorySpinner = this.findViewById<Spinner>(R.id.memorySpinner)
        val authTokenEditText = this.findViewById<EditText>(R.id.authTokenEditText)
        with(defaultSharedPreferences.edit()) {
            putString("serverVersionSpinner", serverVersionSpinner.selectedItem.toString())
            putString("memorySpinner", memorySpinner.selectedItem.toString())
            putString("authTokenEditText", authTokenEditText.text.toString())
            apply()
        }
    }

    private fun startServer() {
        val sha1Array = resources.getStringArray(R.array.server_sha1s)
        val serverVersionSpinner = this.findViewById<Spinner>(R.id.serverVersionSpinner)
        val serverSha1 = sha1Array[serverVersionSpinner.selectedItemPosition]
        val memoryArray = resources.getStringArray(R.array.memory)
        val memorySpinner = this.findViewById<Spinner>(R.id.memorySpinner)
        val memoryString = "" + memoryArray[memorySpinner.selectedItemPosition]
        val authTokenEditText = this.findViewById<EditText>(R.id.authTokenEditText)
        val authToken = authTokenEditText.text.toString()
        val env = HashMap<String, String>()
        env["SHA1"] = serverSha1
        env["AUTH_TOKEN"] = authToken
        env["JAVA_MEMORY"] = "-Xmx" + memoryString + "M -Xms512M"
        val ulaIntent = Intent(this, tech.ula.MainActivity::class.java)
        val app = App("craftbox", "games", "ubuntu", false, true, false, 1)
        ulaIntent.putExtra("app", app)
        ulaIntent.putExtra("env", env)
        this.startActivity(ulaIntent)
        finish()
    }

}