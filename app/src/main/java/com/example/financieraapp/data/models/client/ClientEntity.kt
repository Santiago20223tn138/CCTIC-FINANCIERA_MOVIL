package com.example.financieraapp.data.models.client

import android.os.Parcel
import android.os.Parcelable

data class ClientEntity(
    val message: String,
    val error: Boolean,
    val data: List<Client>
)

data class ClientResponse(
    val message: String,
    val error: Boolean,
    val data: String
)

data class Client(
    val id: Int,
    val nombre: String,
    val numero_cliente: String,
    val paterno: String,
    val materno: String,
    val edad: Int,
    val ingreso_semanal: Int,
    val telefono_1: String,
    val telefono_2: String,
    val correo: String,
    val domicilio: String,
    val domicilio_detalle: String
)

data class ClientBody(
    val nombre: String,
    val numero_cliente: String,
    val paterno: String,
    val materno: String,
    val edad: Int,
    val ingreso_semanal: Double?,
    val telefono_1: String,
    val telefono_2: String?,
    val correo: String,
    val domicilio: String,
    val domicilio_detalle: String,
    val cn_nombre: String?,
    val cn_telefono_1: String?,
    val cn_telefono_2: String?,
    val parentezco: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nombre)
        parcel.writeString(numero_cliente)
        parcel.writeString(paterno)
        parcel.writeString(materno)
        parcel.writeInt(edad)
        parcel.writeValue(ingreso_semanal)
        parcel.writeString(telefono_1)
        parcel.writeString(telefono_2)
        parcel.writeString(correo)
        parcel.writeString(domicilio)
        parcel.writeString(domicilio_detalle)
        parcel.writeString(cn_nombre)
        parcel.writeString(cn_telefono_1)
        parcel.writeString(cn_telefono_2)
        parcel.writeString(parentezco)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ClientBody> {
        override fun createFromParcel(parcel: Parcel): ClientBody {
            return ClientBody(parcel)
        }

        override fun newArray(size: Int): Array<ClientBody?> {
            return arrayOfNulls(size)
        }
    }
}
