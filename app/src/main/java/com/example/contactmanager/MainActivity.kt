package com.example.contactmanager

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.InputType
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

private data class Contact(
    val id: String,
    val name: String,
    val phone: String,
    val email: String
)

class MainActivity : Activity() {

    private val preferencesName = "contact_manager_storage"
    private val contactsKey = "contacts_json"
    private val contacts = mutableListOf<Contact>()

    private lateinit var contactsContainer: LinearLayout
    private lateinit var searchInput: EditText
    private lateinit var countLabel: TextView

    private val pageColor = Color.rgb(244, 246, 251)
    private val primaryColor = Color.rgb(54, 89, 217)
    private val textColor = Color.rgb(28, 35, 53)
    private val secondaryColor = Color.rgb(107, 116, 139)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = pageColor
        window.navigationBarColor = Color.WHITE
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        loadContacts()
        createScreen()
        renderContacts()
    }

    private fun createScreen() {
        val scrollView = ScrollView(this).apply {
            setBackgroundColor(pageColor)
            isFillViewport = true
        }

        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(24), dp(20), dp(28))
        }

        val eyebrow = textView("УПРАВЛЕНИЕ ДАННЫМИ", 11f, secondaryColor, true)
        page.addView(eyebrow)

        val title = textView("Мои контакты", 28f, textColor, true).apply {
            setPadding(0, dp(5), 0, 0)
        }
        page.addView(title)

        val subtitle = textView(
            "Добавим и сохраним контактные данные в одном месте.",
            14f,
            secondaryColor,
            false
        ).apply {
            setPadding(0, dp(7), 0, dp(20))
        }
        page.addView(subtitle)

        val addButton = Button(this).apply {
            text = "+  Добавить контакт"
            textSize = 15f
            isAllCaps = false
            setTextColor(Color.WHITE)
            backgroundTintList = android.content.res.ColorStateList.valueOf(primaryColor)
            setPadding(dp(12), dp(5), dp(12), dp(5))
            setOnClickListener { showContactDialog(null) }
        }
        page.addView(addButton, matchWidth())

        searchInput = EditText(this).apply {
            hint = "Поиск по имени или телефону"
            textSize = 14f
            isSingleLine = true
            inputType = InputType.TYPE_CLASS_TEXT
            setPadding(dp(14), dp(12), dp(14), dp(12))
            background = roundedBackground(Color.WHITE, 12f, Color.rgb(226, 230, 239))
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    renderContacts()
                }
                override fun afterTextChanged(s: Editable?) = Unit
            })
        }
        val searchParams = matchWidth().apply {
            topMargin = dp(18)
            bottomMargin = dp(18)
        }
        page.addView(searchInput, searchParams)

        countLabel = textView("Контакты", 13f, secondaryColor, true).apply {
            setPadding(0, 0, 0, dp(10))
        }
        page.addView(countLabel)

        contactsContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        page.addView(contactsContainer, matchWidth())

        val note = textView(
            "Данные сохраняются на устройстве. Для работы приложения подключение к интернету не требуется.",
            12f,
            secondaryColor,
            false
        ).apply {
            setPadding(0, dp(20), 0, 0)
        }
        page.addView(note)

        scrollView.addView(page)
        setContentView(scrollView)
    }

    private fun renderContacts() {
        if (!::contactsContainer.isInitialized) return
        contactsContainer.removeAllViews()

        val query = if (::searchInput.isInitialized) searchInput.text.toString().trim() else ""
        val filtered = contacts.filter {
            it.name.contains(query, ignoreCase = true) ||
                it.phone.contains(query, ignoreCase = true) ||
                it.email.contains(query, ignoreCase = true)
        }

        countLabel.text = "Контакты: ${filtered.size}"

        if (filtered.isEmpty()) {
            val emptyPanel = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(dp(18), dp(28), dp(18), dp(28))
                background = roundedBackground(Color.WHITE, 14f, Color.rgb(232, 235, 243))
            }
            val emptyTitle = textView(
                if (query.isEmpty()) "Пока нет контактов" else "Ничего не найдено",
                17f,
                textColor,
                true
            )
            val emptyDescription = textView(
                if (query.isEmpty()) "Добавим первый контакт с помощью кнопки выше." else "Изменим поисковый запрос и попробуем ещё раз.",
                13f,
                secondaryColor,
                false
            ).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(7), 0, 0)
            }
            emptyPanel.addView(emptyTitle)
            emptyPanel.addView(emptyDescription)
            contactsContainer.addView(emptyPanel, matchWidth())
            return
        }

        filtered.forEach { contact ->
            contactsContainer.addView(createContactCard(contact), matchWidth().apply {
                bottomMargin = dp(12)
            })
        }
    }

    private fun createContactCard(contact: Contact): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(15), dp(16), dp(13))
            background = roundedBackground(Color.WHITE, 14f, Color.rgb(232, 235, 243))
        }

        card.addView(textView(contact.name, 17f, textColor, true))
        card.addView(textView("Телефон: ${contact.phone}", 13f, secondaryColor, false).apply {
            setPadding(0, dp(7), 0, 0)
        })
        if (contact.email.isNotBlank()) {
            card.addView(textView("Email: ${contact.email}", 13f, secondaryColor, false).apply {
                setPadding(0, dp(4), 0, 0)
            })
        }

        val actions = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.END
            setPadding(0, dp(10), 0, 0)
        }

        val editButton = Button(this).apply {
            text = "Изменить"
            textSize = 12f
            isAllCaps = false
            setTextColor(primaryColor)
            backgroundTintList = android.content.res.ColorStateList.valueOf(Color.rgb(235, 239, 255))
            setOnClickListener { showContactDialog(contact) }
        }
        val deleteButton = Button(this).apply {
            text = "Удалить"
            textSize = 12f
            isAllCaps = false
            setTextColor(Color.rgb(178, 54, 67))
            backgroundTintList = android.content.res.ColorStateList.valueOf(Color.rgb(255, 238, 240))
            setOnClickListener { confirmDelete(contact) }
        }

        actions.addView(editButton)
        actions.addView(deleteButton, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { leftMargin = dp(8) })
        card.addView(actions)
        return card
    }

    private fun showContactDialog(contact: Contact?) {
        val form = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(8), dp(20), 0)
        }

        val nameInput = createFormInput("Имя", InputType.TYPE_CLASS_TEXT).apply {
            setText(contact?.name.orEmpty())
        }
        val phoneInput = createFormInput("Номер телефона", InputType.TYPE_CLASS_PHONE).apply {
            setText(contact?.phone.orEmpty())
        }
        val emailInput = createFormInput("Электронная почта", InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS or InputType.TYPE_CLASS_TEXT).apply {
            setText(contact?.email.orEmpty())
        }

        form.addView(nameInput)
        form.addView(phoneInput, formInputParams())
        form.addView(emailInput, formInputParams())

        val dialog = AlertDialog.Builder(this)
            .setTitle(if (contact == null) "Новый контакт" else "Редактирование контакта")
            .setView(form)
            .setNegativeButton("Отмена", null)
            .setPositiveButton("Сохранить", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val name = nameInput.text.toString().trim()
                val phone = phoneInput.text.toString().trim()
                val email = emailInput.text.toString().trim()

                if (name.isBlank()) {
                    nameInput.error = "Укажем имя контакта"
                    return@setOnClickListener
                }
                if (phone.isBlank()) {
                    phoneInput.error = "Укажем номер телефона"
                    return@setOnClickListener
                }

                if (contact == null) {
                    contacts.add(Contact(UUID.randomUUID().toString(), name, phone, email))
                    Toast.makeText(this, "Контакт добавлен", Toast.LENGTH_SHORT).show()
                } else {
                    val index = contacts.indexOfFirst { it.id == contact.id }
                    if (index >= 0) contacts[index] = contact.copy(name = name, phone = phone, email = email)
                    Toast.makeText(this, "Изменения сохранены", Toast.LENGTH_SHORT).show()
                }
                saveContacts()
                renderContacts()
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun confirmDelete(contact: Contact) {
        AlertDialog.Builder(this)
            .setTitle("Удаление контакта")
            .setMessage("Удалим контакт «${contact.name}»?")
            .setNegativeButton("Отмена", null)
            .setPositiveButton("Удалить") { _, _ ->
                contacts.removeAll { it.id == contact.id }
                saveContacts()
                renderContacts()
                Toast.makeText(this, "Контакт удалён", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun createFormInput(hintText: String, type: Int): EditText {
        return EditText(this).apply {
            hint = hintText
            inputType = type
            isSingleLine = true
            textSize = 15f
            setPadding(dp(12), dp(11), dp(12), dp(11))
            background = roundedBackground(Color.WHITE, 10f, Color.rgb(216, 222, 235))
        }
    }

    private fun formInputParams(): LinearLayout.LayoutParams = matchWidth().apply {
        topMargin = dp(12)
    }

    private fun loadContacts() {
        val raw = getSharedPreferences(preferencesName, MODE_PRIVATE).getString(contactsKey, "[]") ?: "[]"
        try {
            val array = JSONArray(raw)
            for (index in 0 until array.length()) {
                val item = array.optJSONObject(index) ?: continue
                contacts.add(
                    Contact(
                        id = item.optString("id", UUID.randomUUID().toString()),
                        name = item.optString("name"),
                        phone = item.optString("phone"),
                        email = item.optString("email")
                    )
                )
            }
        } catch (_: Exception) {
            contacts.clear()
        }
    }

    private fun saveContacts() {
        val array = JSONArray()
        contacts.forEach { contact ->
            array.put(JSONObject().apply {
                put("id", contact.id)
                put("name", contact.name)
                put("phone", contact.phone)
                put("email", contact.email)
            })
        }
        getSharedPreferences(preferencesName, MODE_PRIVATE)
            .edit()
            .putString(contactsKey, array.toString())
            .apply()
    }

    private fun textView(textValue: String, size: Float, color: Int, bold: Boolean): TextView {
        return TextView(this).apply {
            text = textValue
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(typeface, Typeface.BOLD)
        }
    }

    private fun roundedBackground(color: Int, radiusDp: Float, strokeColor: Int): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radiusDp).toFloat()
            setStroke(dp(1), strokeColor)
        }
    }

    private fun matchWidth(): LinearLayout.LayoutParams = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
    private fun dp(value: Float): Int = (value * resources.displayMetrics.density).toInt()
}
