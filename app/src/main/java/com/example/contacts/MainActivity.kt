package com.example.contacts

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // 1) Les données : une liste MODIFIABLE (mutableListOf) de chaînes simples
    private val contacts = mutableListOf(
        "Ahmed Ben Ali",
        "Sarra Trabelsi",
        "Mohamed Gharbi",
        "Ines Jaziri",
        "Youssef Mansouri",
        "Amira Chaabane",
        "Karim Bouazizi",
        "Nour Hammami"
    )

    // 2) L'adaptateur fait le lien entre les données et la ListView
    private lateinit var adapter: ArrayAdapter<String>

    // 3) Les vues
    private lateinit var etContact: EditText
    private lateinit var btnModifier: Button
    private lateinit var btnAjouter: Button
    private lateinit var lvContacts: ListView

    // Position de l'élément sélectionné (-1 = aucune sélection)
    private var positionSelectionnee = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Récupération des vues déclarées dans activity_main.xml
        etContact = findViewById(R.id.etContact)
        btnModifier = findViewById(R.id.btnModifier)
        btnAjouter = findViewById(R.id.btnAjouter)
        lvContacts = findViewById(R.id.lvContacts)

        // Exercice 4 : liste triée dès le départ (sans tenir compte des majuscules)
        contacts.sortWith(String.CASE_INSENSITIVE_ORDER)

        // Création de l'adaptateur avec un layout Android prédéfini
        // simple_list_item_activated_1 met en surbrillance l'élément sélectionné
        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_activated_1,
            contacts
        )
        lvContacts.adapter = adapter

        // Clic sur un élément de la liste -> on copie son texte dans l'EditText
        lvContacts.setOnItemClickListener { _, _, position, _ ->
            positionSelectionnee = position
            lvContacts.setItemChecked(position, true)
            etContact.setText(contacts[position])
            etContact.setSelection(etContact.text.length) // curseur à la fin
            etContact.requestFocus()
        }

        // Exercice 2 : appui long -> suppression après confirmation
        lvContacts.setOnItemLongClickListener { _, _, position, _ ->
            supprimerContact(position)
            true
        }

        // Clic sur le bouton "Modifier"
        btnModifier.setOnClickListener { modifierContact() }

        // Exercice 1 : clic sur le bouton "Ajouter"
        btnAjouter.setOnClickListener { ajouterContact() }

        // Bonus : la touche "Terminé" du clavier valide aussi la modification
        etContact.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                modifierContact()
                true
            } else {
                false
            }
        }
    }

    private fun modifierContact() {
        // Vérification 1 : un élément doit être sélectionné
        if (positionSelectionnee == -1) {
            Toast.makeText(this, R.string.msg_aucune_selection, Toast.LENGTH_SHORT).show()
            return
        }

        // Vérification 2 : le texte ne doit pas être vide
        val nouveauNom = etContact.text.toString().trim()
        if (nouveauNom.isEmpty()) {
            etContact.error = getString(R.string.msg_nom_vide)
            return
        }

        // Exercice 3 : on refuse un nom qui existe déjà (sauf si c'est le même contact)
        val positionExistante = contacts.indexOf(nouveauNom)
        if (positionExistante != -1 && positionExistante != positionSelectionnee) {
            etContact.error = getString(R.string.msg_doublon)
            return
        }

        // Mise à jour de la donnée...
        contacts[positionSelectionnee] = nouveauNom

        // Exercice 4 : tri alphabétique, puis on resélectionne le contact modifié
        contacts.sortWith(String.CASE_INSENSITIVE_ORDER)
        positionSelectionnee = contacts.indexOf(nouveauNom)
        lvContacts.setItemChecked(positionSelectionnee, true)

        // ...puis on prévient l'adaptateur pour qu'il redessine la liste
        adapter.notifyDataSetChanged()

        Toast.makeText(this, getString(R.string.msg_modifie, nouveauNom), Toast.LENGTH_SHORT).show()
    }

    // Exercice 1 : ajouter le texte de l'EditText comme nouveau contact
    private fun ajouterContact() {
        val nom = etContact.text.toString().trim()
        if (nom.isEmpty()) {
            etContact.error = getString(R.string.msg_nom_vide)
            return
        }
        if (contacts.contains(nom)) {
            etContact.error = getString(R.string.msg_doublon)
            return
        }

        contacts.add(nom)
        contacts.sortWith(String.CASE_INSENSITIVE_ORDER)
        adapter.notifyDataSetChanged()

        annulerSelection()
        Toast.makeText(this, getString(R.string.msg_ajoute, nom), Toast.LENGTH_SHORT).show()
    }

    // Exercice 2 : suppression avec une boîte de confirmation
    private fun supprimerContact(position: Int) {
        val nom = contacts[position]

        AlertDialog.Builder(this)
            .setTitle(R.string.titre_supprimer)
            .setMessage(getString(R.string.msg_confirmer_suppression, nom))
            .setPositiveButton(R.string.oui) { _, _ ->
                contacts.removeAt(position)
                adapter.notifyDataSetChanged()
                annulerSelection()
                Toast.makeText(this, getString(R.string.msg_supprime, nom), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.non, null)
            .show()
    }

    // Plus aucun contact sélectionné : on vide le champ et la surbrillance
    private fun annulerSelection() {
        positionSelectionnee = -1
        lvContacts.clearChoices()
        lvContacts.requestLayout()
        etContact.text.clear()
    }
}
