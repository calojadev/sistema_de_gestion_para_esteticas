const API = "http://localhost:8080/api/servicios";
let idAEliminar = null;
let modoEdicion = false;

document.addEventListener("DOMContentLoaded", cargarTodos);

async function cargarTodos() {
  try {
    const res = await fetch(API);
    const servicios = await res.json();
    renderTabla(servicios);
  } catch (e) {
    mostrarAlerta("Error al conectar con el servidor.", "danger");
  }
}

async function buscarPorNombre() {
  const nombre = document.getElementById("inputBuscarNombre").value.trim();
  if (!nombre) {
    cargarTodos();
    return;
  }
  try {
    const res = await fetch(`${API}/buscar?nombre=${nombre}`);
    const servicios = await res.json();
    if (servicios.length === 0) {
      mostrarAlerta("No se encontraron servicios con ese nombre.", "warning");
    }
    renderTabla(servicios);
  } catch (e) {
    mostrarAlerta("Error al buscar.", "danger");
  }
}

function renderTabla(servicios) {
  const tbody = document.getElementById("tablaServicios");
  if (servicios.length === 0) {
    tbody.innerHTML = `<tr><td colspan="5" class="text-center text-muted py-4">No se encontraron servicios.</td></tr>`;
    return;
  }
  tbody.innerHTML = servicios
    .map(
      (s) => `
    <tr>
      <td>${s.nombre}</td>
      <td>${s.descripcion ?? "—"}</td>
      <td>Gs. ${Number(s.precio).toLocaleString()}</td>
      <td>${s.duracionMinutos ?? "—"} min</td>
      <td class="text-center">
        <button class="btn btn-warning btn-accion me-1" onclick="abrirModalEditar(${JSON.stringify(s).replace(/"/g, "&quot;")})">
          <i class="bi bi-pencil"></i>
        </button>
        <button class="btn btn-danger btn-accion" onclick="abrirModalEliminar(${s.id})">
          <i class="bi bi-trash"></i>
        </button>
      </td>
    </tr>
  `,
    )
    .join("");
}

function abrirModalNuevo() {
  modoEdicion = false;
  document.getElementById("modalTitulo").textContent = "Nuevo Servicio";
  limpiarFormulario();
  new bootstrap.Modal(document.getElementById("modalServicio")).show();
}

function abrirModalEditar(s) {
  modoEdicion = true;
  document.getElementById("modalTitulo").textContent = "Editar Servicio";
  document.getElementById("servicioId").value = s.id;
  document.getElementById("inputNombre").value = s.nombre;
  document.getElementById("inputDescripcion").value = s.descripcion ?? "";
  document.getElementById("inputPrecio").value = s.precio;
  document.getElementById("inputDuracion").value = s.duracionMinutos ?? "";
  new bootstrap.Modal(document.getElementById("modalServicio")).show();
}

async function guardarServicio() {
  const nombre = document.getElementById("inputNombre").value.trim();
  const precio = document.getElementById("inputPrecio").value.trim();

  if (!nombre || !precio) {
    mostrarAlerta("Nombre y precio son obligatorios.", "warning");
    return;
  }

  if (parseFloat(precio) < 0) {
    mostrarAlerta("El precio no puede ser negativo.", "warning");
    return;
  }

  const datos = {
    nombre,
    descripcion:
      document.getElementById("inputDescripcion").value.trim() || null,
    precio: parseFloat(precio),
    duracionMinutos: document.getElementById("inputDuracion").value
      ? parseInt(document.getElementById("inputDuracion").value)
      : null,
  };

  try {
    let res;
    if (modoEdicion) {
      const id = document.getElementById("servicioId").value;
      res = await fetch(`${API}/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(datos),
      });
    } else {
      res = await fetch(API, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(datos),
      });
    }

    if (res.ok) {
      bootstrap.Modal.getInstance(
        document.getElementById("modalServicio"),
      ).hide();
      mostrarAlerta(
        modoEdicion
          ? "Servicio actualizado correctamente."
          : "Servicio creado correctamente.",
        "success",
      );
      cargarTodos();
    } else {
      mostrarAlerta(
        "Error al guardar. Verifica que el nombre no esté duplicado.",
        "danger",
      );
    }
  } catch (e) {
    mostrarAlerta("Error al conectar con el servidor.", "danger");
  }
}

function abrirModalEliminar(id) {
  idAEliminar = id;
  new bootstrap.Modal(document.getElementById("modalEliminar")).show();
}

async function confirmarEliminar() {
  try {
    const res = await fetch(`${API}/${idAEliminar}`, { method: "DELETE" });
    bootstrap.Modal.getInstance(
      document.getElementById("modalEliminar"),
    ).hide();
    if (res.ok) {
      mostrarAlerta("Servicio eliminado correctamente.", "success");
      cargarTodos();
    } else {
      mostrarAlerta("Error al eliminar el servicio.", "danger");
    }
  } catch (e) {
    mostrarAlerta("Error al conectar con el servidor.", "danger");
  }
}

function limpiarFormulario() {
  [
    "servicioId",
    "inputNombre",
    "inputDescripcion",
    "inputPrecio",
    "inputDuracion",
  ].forEach((id) => (document.getElementById(id).value = ""));
}

function mostrarAlerta(mensaje, tipo) {
  const alerta = document.getElementById("alerta");
  alerta.className = `alert alert-${tipo}`;
  alerta.textContent = mensaje;
  alerta.classList.remove("d-none");
  setTimeout(() => alerta.classList.add("d-none"), 4000);
}
