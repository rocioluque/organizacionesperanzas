package com.rocio.organizacionesperanzas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class UserManagementActivity : AppCompatActivity(), UserManagementAdapter.OnUserActionClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserManagementAdapter
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
        lifecycleScope.launch {
            val result = AppRepository.getUsers()
            users.clear()
            users.addAll(result)
            adapter.updateUsers(users)
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

        lifecycleScope.launch {
            val allTeams = AppRepository.getAllTeams()
            val teamNames = allTeams.map { it.name }.toTypedArray()
            val checkedTeams = BooleanArray(allTeams.size) {
                isEditing && user!!.assignedTeams?.contains(allTeams[it].id) == true
            }

            val inflater = LayoutInflater.from(this@UserManagementActivity)
            val dialogView = inflater.inflate(R.layout.dialog_add_user, null)
            val usernameInput = dialogView.findViewById<EditText>(R.id.username_input)
            val passwordInput = dialogView.findViewById<EditText>(R.id.password_input)
            val roleGroup = dialogView.findViewById<RadioGroup>(R.id.role_radio_group)
            val teamsHeader = dialogView.findViewById<View>(R.id.teams_header) // For visibility toggle

            if (isEditing) {
                usernameInput.setText(user!!.username)
                passwordInput.hint = "Dejar en blanco para no cambiar"
                if (user.role == UserRole.DELEGATE) {
                    roleGroup.check(R.id.radio_delegate)
                } else {
                    roleGroup.check(R.id.radio_organizer)
                }
            }

            val teamsDialog = AlertDialog.Builder(this@UserManagementActivity)
                .setTitle(title)
                .setView(dialogView)
                .setMultiChoiceItems(teamNames, checkedTeams) { _, which, isChecked ->
                    checkedTeams[which] = isChecked
                }
                .setPositiveButton(if (isEditing) "Guardar" else "Añadir") { _, _ ->
                    val username = usernameInput.text.toString().trim()
                    val password = passwordInput.text.toString().trim()
                    val selectedRole = if (roleGroup.checkedRadioButtonId == R.id.radio_delegate) UserRole.DELEGATE else UserRole.ORGANIZER
                    val selectedTeams = mutableListOf<String>()
                    if (selectedRole == UserRole.DELEGATE) {
                        for (i in checkedTeams.indices) {
                            if (checkedTeams[i]) {
                                selectedTeams.add(allTeams[i].id)
                            }
                        }
                    }

                    if (username.isNotEmpty() && (password.isNotEmpty() || isEditing)) {
                        lifecycleScope.launch {
                            val userToSave = CreateUserRequest(username, password, selectedRole, selectedTeams)
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
                }
                .setNegativeButton("Cancelar", null)
                .create()

            roleGroup.setOnCheckedChangeListener { _, checkedId ->
                val isDelegate = checkedId == R.id.radio_delegate
                // This part is tricky with MultiChoiceItems, visibility needs to be managed carefully.
                // For simplicity, we'll just show/hide the header.
                teamsHeader.isVisible = isDelegate
            }
            
            // Set initial visibility
            teamsHeader.isVisible = roleGroup.checkedRadioButtonId == R.id.radio_delegate

            teamsDialog.show()
        }
    }
}