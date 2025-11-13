package com.rocio.organizacionesperanzas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class UserManagementActivity : AppCompatActivity(), UserManagementAdapter.OnUserActionClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserManagementAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyState: LinearLayout
    private val users = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_management)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Gestionar Usuarios"
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        recyclerView = findViewById(R.id.users_recycler_view)
        progressBar = findViewById(R.id.loading_progress)
        emptyState = findViewById(R.id.empty_state)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = UserManagementAdapter(users, this)
        recyclerView.adapter = adapter

        val fab: FloatingActionButton = findViewById(R.id.add_user_fab)
        fab.setOnClickListener {
            showAddOrEditUserDialog(null)
        }

        loadUsers()
    }

    private fun loadUsers() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        emptyState.visibility = View.GONE

        lifecycleScope.launch {
            val result = AppRepository.getUsers()
            users.clear()
            users.addAll(result)
            adapter.updateUsers(users)

            progressBar.visibility = View.GONE
            if (users.isEmpty()) {
                emptyState.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
            }
        }
    }

    override fun onEditUser(user: User) {
        showAddOrEditUserDialog(user)
    }

    override fun onDeleteUser(user: User) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Estás seguro de que quieres eliminar al usuario '${user.username}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch {
                    val success = AppRepository.deleteUser(user.id)
                    if (success) {
                        Toast.makeText(this@UserManagementActivity, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                        loadUsers()
                    } else {
                        Toast.makeText(this@UserManagementActivity, "Error al eliminar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showAddOrEditUserDialog(user: User?) {
        val isEditing = user != null
        val title = if (isEditing) "Editar Usuario" else "Añadir Usuario"

        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_add_user, null)
        val usernameInput = dialogView.findViewById<EditText>(R.id.username_input)
        val passwordInput = dialogView.findViewById<EditText>(R.id.password_input)
        val roleGroup = dialogView.findViewById<RadioGroup>(R.id.role_radio_group)

        if (isEditing) {
            usernameInput.setText(user!!.username)
            passwordInput.hint = "Dejar en blanco para no cambiar"
            when (user.role) {
                UserRole.DELEGATE -> roleGroup.check(R.id.radio_delegate)
                UserRole.ORGANIZER -> roleGroup.check(R.id.radio_organizer)
                else -> {}
            }
        } else {
            roleGroup.check(R.id.radio_delegate)
        }

        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(dialogView)
            .setPositiveButton(if (isEditing) "Guardar" else "Añadir") { _, _ ->
                val username = usernameInput.text.toString().trim()
                val password = passwordInput.text.toString().trim()
                val selectedRole = if (roleGroup.checkedRadioButtonId == R.id.radio_delegate) UserRole.DELEGATE else UserRole.ORGANIZER

                if (username.isEmpty()) {
                    Toast.makeText(this, "El nombre de usuario no puede estar vacío", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                if (!isEditing && password.isEmpty()) {
                    Toast.makeText(this, "La contraseña es obligatoria para nuevos usuarios", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                lifecycleScope.launch {
                    val userToSave = CreateUserRequest(username = username, password = password, role = selectedRole)
                    val result = if (isEditing) {
                        AppRepository.updateUser(user!!.id, userToSave)
                    } else {
                        AppRepository.addUser(userToSave)
                    }

                    if (result != null) {
                        Toast.makeText(this@UserManagementActivity, "Guardado con éxito", Toast.LENGTH_SHORT).show()
                        loadUsers()
                    } else {
                        Toast.makeText(this@UserManagementActivity, "Error al guardar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}