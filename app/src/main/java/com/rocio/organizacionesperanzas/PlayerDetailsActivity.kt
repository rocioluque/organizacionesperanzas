package com.rocio.organizacionesperanzas

import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.request.CachePolicy
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PlayerDetailsActivity : AppCompatActivity() {

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var birthDateEditText: EditText
    private lateinit var playerPhotoImageView: ImageView
    private lateinit var saveButton: Button
    private lateinit var uploadPlayerPhotoButton: Button
    private lateinit var organizerActionsLayout: MaterialCardView

    private var currentPlayer: Player? = null
    private var isEditMode = false
    private var userRole: UserRole? = null

    private var selectedPlayerPhotoUri: Uri? = null

    private val selectPlayerImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedPlayerPhotoUri = it
            playerPhotoImageView.load(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_details)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        firstNameEditText = findViewById(R.id.first_name_details)
        lastNameEditText = findViewById(R.id.last_name_details)
        birthDateEditText = findViewById(R.id.birth_date_details)
        playerPhotoImageView = findViewById(R.id.player_photo)
        saveButton = findViewById(R.id.save_button)
        uploadPlayerPhotoButton = findViewById(R.id.upload_player_photo_button)
        organizerActionsLayout = findViewById(R.id.organizer_actions_card)

        val approveButton = findViewById<Button>(R.id.approve_button)
        val rejectButton = findViewById<Button>(R.id.reject_button)

        val playerId = intent.getStringExtra("PLAYER_ID")
        val categoryId = intent.getStringExtra("CATEGORY_ID")
        val teamId = intent.getStringExtra("TEAM_ID")
        userRole = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("USER_ROLE", UserRole::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("USER_ROLE") as? UserRole
        }

        isEditMode = playerId != null

        if (isEditMode) {
            loadPlayerDetails(playerId!!)
            title = "Editar Jugador"
        } else {
            title = "Añadir Jugador"
        }

        setupVisibility() // Setup visibility on create

        uploadPlayerPhotoButton.setOnClickListener { selectPlayerImage.launch("image/*") }
        birthDateEditText.setOnClickListener { showDatePickerDialog() }
        saveButton.setOnClickListener { handleSave(categoryId, teamId) }
        approveButton.setOnClickListener { updateStatus(PlayerStatus.APPROVED) }
        rejectButton.setOnClickListener { updateStatus(PlayerStatus.REJECTED) }
    }

    override fun onResume() {
        super.onResume()
        setupVisibility() // Re-apply visibility rules when the activity is resumed
    }

    private fun setupVisibility() {
        when (userRole) {
            UserRole.DELEGATE -> organizerActionsLayout.visibility = View.GONE
            UserRole.ORGANIZER -> {
                saveButton.visibility = View.GONE
                uploadPlayerPhotoButton.visibility = View.GONE
                setFieldsEditable(false)
                organizerActionsLayout.visibility = View.VISIBLE // Ensure it's visible
            }
            else -> {
                organizerActionsLayout.visibility = View.GONE
                saveButton.visibility = View.GONE
                setFieldsEditable(false)
            }
        }
    }

    private fun setFieldsEditable(isEditable: Boolean) {
        firstNameEditText.isEnabled = isEditable
        lastNameEditText.isEnabled = isEditable
        birthDateEditText.isEnabled = isEditable
    }

    private fun loadPlayerDetails(playerId: String) {
        lifecycleScope.launch {
            currentPlayer = AppRepository.getPlayerById(playerId)
            currentPlayer?.let {
                firstNameEditText.setText(it.firstName)
                lastNameEditText.setText(it.lastName)
                birthDateEditText.setText(it.birthDate)

                Log.d("PlayerDetails", "Loading photoUrl: ${it.photoUrl}")
                it.photoUrl?.let {
                    playerPhotoImageView.load(it) {
                        crossfade(true)
                        placeholder(R.drawable.ic_launcher_foreground)
                        error(android.R.drawable.ic_menu_gallery)
                        memoryCachePolicy(CachePolicy.DISABLED)
                    }
                } ?: playerPhotoImageView.setImageResource(android.R.drawable.ic_menu_camera)
            }
        }
    }

    private fun handleSave(categoryId: String?, teamId: String?) {
        val firstName = firstNameEditText.text.toString().trim()
        val lastName = lastNameEditText.text.toString().trim()
        val birthDate = birthDateEditText.text.toString().trim()

        if (firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(this, "Nombre y apellido son requeridos", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val playerPhotoUrlDeferred = async { selectedPlayerPhotoUri?.let { AppRepository.uploadPhotoToCloudinary(it) } }

            val newPlayerPhotoUrl = playerPhotoUrlDeferred.await() ?: currentPlayer?.photoUrl

            Log.d("PlayerDetails", "Saving photoUrl: $newPlayerPhotoUrl")

            if (isEditMode) {
                val updatedPlayer = currentPlayer!!.copy(
                    firstName = firstName,
                    lastName = lastName,
                    birthDate = birthDate,
                    photoUrl = newPlayerPhotoUrl
                )
                AppRepository.updatePlayerDetails(updatedPlayer.id, updatedPlayer)
                Toast.makeText(this@PlayerDetailsActivity, "Jugador guardado", Toast.LENGTH_SHORT).show()
            } else {
                if (categoryId == null) {
                    Toast.makeText(this@PlayerDetailsActivity, "Error: ID de categoría no encontrado", Toast.LENGTH_LONG).show()
                    return@launch
                }
                val newPlayer = Player(
                    categoryId = categoryId,
                    teamId = teamId,
                    firstName = firstName,
                    lastName = lastName,
                    birthDate = birthDate,
                    photoUrl = newPlayerPhotoUrl
                )
                AppRepository.addPlayer(newPlayer)
                Toast.makeText(this@PlayerDetailsActivity, "Jugador añadido", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }

    private fun uriToBitmap(uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
        } else {
            @Suppress("DEPRECATION")
            android.provider.MediaStore.Images.Media.getBitmap(contentResolver, uri)
        }
    }

    private fun updateStatus(newStatus: PlayerStatus) {
        if (isEditMode) {
            lifecycleScope.launch {
                AppRepository.updatePlayerStatus(currentPlayer!!.id, newStatus)
                val message = if (newStatus == PlayerStatus.APPROVED) "Jugador aprobado" else "Jugador rechazado"
                Toast.makeText(this@PlayerDetailsActivity, message, Toast.LENGTH_SHORT).show()
                // Do not finish the activity, so the admin can see the result and change it again if needed
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val format = "dd/MM/yyyy"
            val sdf = SimpleDateFormat(format, Locale.US)
            birthDateEditText.setText(sdf.format(calendar.time))
        }
        DatePickerDialog(this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }
}