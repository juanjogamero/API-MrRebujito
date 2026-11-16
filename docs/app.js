/* MrRebujito · demo local e interactiva
   Los datos son ficticios. La autorización real reside en el backend Java. */
(() => {
  "use strict";
  const KEY="mrrebujito-portfolio-v1";
  const SEED=Object.freeze({
    casetas:[
      {id:1,nombre:"La Flor de Abril",razon:"Asociación La Flor de Abril",aforo:5,publica:true,socios:[1,2],productos:[
        {id:1,nombre:"Rebujito tradicional",tipo:"BEBIDA",precio:3.5},
        {id:2,nombre:"Montadito de pringá",tipo:"COMIDA",precio:4.2},
        {id:3,nombre:"Tortilla de patatas",tipo:"COMIDA",precio:4.5}
      ]},
      {id:2,nombre:"El Farolillo",razon:"Peña Cultural El Farolillo",aforo:8,publica:true,socios:[3,4,5],productos:[
        {id:4,nombre:"Rebujito sin alcohol",tipo:"BEBIDA",precio:3},
        {id:5,nombre:"Pescaito frito",tipo:"COMIDA",precio:8.5}
      ]},
      {id:3,nombre:"Los Alcores",razon:"Asociación Los Alcores",aforo:6,publica:false,socios:[6],productos:[
        {id:6,nombre:"Croquetas caseras",tipo:"COMIDA",precio:6},
        {id:7,nombre:"Refresco",tipo:"BEBIDA",precio:2.5}
      ]},
      {id:4,nombre:"Rincón del Real",razon:"Sociedad Rincón del Real",aforo:10,publica:true,socios:[7,8],productos:[
        {id:8,nombre:"Salmorejo",tipo:"COMIDA",precio:5.5},
        {id:9,nombre:"Agua mineral",tipo:"BEBIDA",precio:1.8}
      ]}
    ],
    socios:[
      {id:1,nombre:"María",apellido:"López"},{id:2,nombre:"Javier",apellido:"Ruiz"},
      {id:3,nombre:"Carmen",apellido:"Pérez"},{id:4,nombre:"Manuel",apellido:"García"},
      {id:5,nombre:"Lucía",apellido:"Torres"},{id:6,nombre:"Antonio",apellido:"Molina"},
      {id:7,nombre:"Rocío",apellido:"Castro"},{id:8,nombre:"Pablo",apellido:"Jiménez"},
      {id:9,nombre:"Elena",apellido:"Martín"},{id:10,nombre:"Daniel",apellido:"Romero"}
    ],
    ayuntamientos:[
      {id:1,nombre:"Ayuntamiento del Real",limite:2},
      {id:2,nombre:"Ayuntamiento de la Campiña",limite:3}
    ],
    solicitudes:[
      {id:101,casetaId:1,aytoId:1,estado:"APROBADA"},
      {id:102,casetaId:2,aytoId:1,estado:"PENDIENTE"},
      {id:103,casetaId:3,aytoId:2,estado:"RECHAZADA"},
      {id:104,casetaId:4,aytoId:2,estado:"PENDIENTE"}
    ]
  });

  const seed=()=>JSON.parse(JSON.stringify(SEED));
  function counts(db){
    return {
      casetas:db.casetas.length,
      socios:db.casetas.reduce((n,c)=>n+c.socios.length,0),
      aprobadas:db.solicitudes.filter(s=>s.estado==="APROBADA").length,
      pendientes:db.solicitudes.filter(s=>s.estado==="PENDIENTE").length
    };
  }
  function crearSolicitud(db,casetaId,aytoId,role){
    if(role!=="CASETA"||casetaId!==1)return {ok:false,error:"Selecciona la vista Caseta para solicitar licencias de La Flor de Abril."};
    if(!db.casetas.some(c=>c.id===casetaId)||!db.ayuntamientos.some(a=>a.id===aytoId))
      return {ok:false,error:"Caseta o ayuntamiento no encontrado."};
    if(db.solicitudes.some(s=>s.casetaId===casetaId&&s.aytoId===aytoId&&
      (s.estado==="PENDIENTE"||s.estado==="APROBADA")))
      return {ok:false,error:"Ya existe una solicitud pendiente o aprobada para este ayuntamiento."};
    const id=Math.max(100,...db.solicitudes.map(s=>s.id))+1;
    const solicitud={id,casetaId,aytoId,estado:"PENDIENTE"};
    db.solicitudes.push(solicitud);
    return {ok:true,solicitud};
  }
  function decidirLicencia(db,id,estado,role){
    const s=db.solicitudes.find(s=>s.id===id);
    if(!s)return {ok:false,error:"Solicitud no encontrada."};
    if(role==="CASETA"&&estado==="ELIMINAR"&&s.casetaId===1&&s.estado==="PENDIENTE"){
      db.solicitudes=db.solicitudes.filter(item=>item.id!==id);return {ok:true};
    }
    if(role!=="AYUNTAMIENTO"||s.aytoId!==1)
      return {ok:false,error:"Solo el ayuntamiento responsable puede resolver esta solicitud."};
    if(s.estado!=="PENDIENTE")
      return {ok:false,error:"Solo se pueden resolver solicitudes pendientes."};
    if(estado!=="APROBADA"&&estado!=="RECHAZADA")
      return {ok:false,error:"Estado no admitido."};
    const ayto=db.ayuntamientos.find(a=>a.id===s.aytoId);
    const aprobadas=db.solicitudes.filter(q=>q.aytoId===s.aytoId&&q.estado==="APROBADA").length;
    if(estado==="APROBADA"&&aprobadas>=ayto.limite)
      return {ok:false,error:"No quedan licencias disponibles en este ayuntamiento."};
    s.estado=estado;return {ok:true,solicitud:s};
  }
  function anadirSocio(db,casetaId,socioId,role){
    if(role!=="CASETA"||casetaId!==1)
      return {ok:false,error:"Solo la caseta responsable puede modificar su lista de socios."};
    const caseta=db.casetas.find(c=>c.id===casetaId);
    if(!caseta||!db.socios.some(s=>s.id===socioId))
      return {ok:false,error:"Caseta o socio no encontrado."};
    if(caseta.socios.includes(socioId))return {ok:false,error:"El socio ya pertenece a esta caseta."};
    if(caseta.socios.length>=caseta.aforo)return {ok:false,error:"Aforo completo: no se pueden añadir más socios."};
    caseta.socios.push(socioId);return {ok:true};
  }
  function quitarSocio(db,casetaId,socioId,role){
    if(role!=="CASETA"||casetaId!==1)return {ok:false,error:"No tienes permisos sobre esta caseta."};
    const caseta=db.casetas.find(c=>c.id===casetaId);
    if(!caseta||!caseta.socios.includes(socioId))return {ok:false,error:"El socio no está en la caseta."};
    caseta.socios=caseta.socios.filter(id=>id!==socioId);return {ok:true};
  }
  function anadirProducto(db,casetaId,role,nombre,tipo,precio){
    if(role!=="CASETA"||casetaId!==1)return {ok:false,error:"Solo la caseta propietaria puede editar la carta."};
    const caseta=db.casetas.find(c=>c.id===casetaId);
    const p=Number(precio),nombreLimpio=String(nombre||"").trim();
    if(!caseta||!nombreLimpio||nombreLimpio.length>70||!["COMIDA","BEBIDA"].includes(tipo)||
       !Number.isFinite(p)||p<0||p>1000)
      return {ok:false,error:"Introduce un nombre, tipo y precio válidos."};
    const id=Math.max(0,...db.casetas.flatMap(c=>c.productos.map(p=>p.id)))+1;
    const producto={id,nombre:nombreLimpio,tipo,precio:Math.round(p*100)/100};
    caseta.productos.push(producto);return {ok:true,producto};
  }
  function quitarProducto(db,casetaId,id,role){
    if(role!=="CASETA"||casetaId!==1)return {ok:false,error:"Solo la caseta propietaria puede editar la carta."};
    const caseta=db.casetas.find(c=>c.id===casetaId);
    if(!caseta||!caseta.productos.some(p=>p.id===id))return {ok:false,error:"Producto no encontrado."};
    caseta.productos=caseta.productos.filter(p=>p.id!==id);return {ok:true};
  }
  const API=Object.freeze({seed,counts,crearSolicitud,decidirLicencia,anadirSocio,quitarSocio,anadirProducto,quitarProducto});
  if(typeof window!=="undefined")window.MrRebujitoDemo=API;
  if(typeof document==="undefined")return;

  function load(){
    try{
      const saved=JSON.parse(localStorage.getItem(KEY));
      if(saved&&Array.isArray(saved.casetas)&&Array.isArray(saved.socios)&&
         Array.isArray(saved.ayuntamientos)&&Array.isArray(saved.solicitudes))return saved;
    }catch(_){}
    return seed();
  }
  let db=load(),role="INVITADO",active="inicio",search="",type="all",licenseFilter="all",memberCaseta=1,menuCaseta=1;
  const $=id=>document.getElementById(id);
  const esc=s=>String(s??"").replace(/[&<>"']/g,x=>({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"}[x]));
  const euro=n=>new Intl.NumberFormat("es-ES",{style:"currency",currency:"EUR"}).format(n);
  const caseta=id=>db.casetas.find(c=>c.id===id);
  const ayto=id=>db.ayuntamientos.find(a=>a.id===id);
  const socio=id=>db.socios.find(s=>s.id===id);
  const title=s=>s==="APROBADA"?"Aprobada":s==="RECHAZADA"?"Rechazada":"Pendiente";
  const badge=s=>'<span class="status '+esc(s)+'">'+title(s)+'</span>';
  function save(){try{localStorage.setItem(KEY,JSON.stringify(db));}catch(_){}}
  let toastTimeout=0;
  function toast(message){const el=$("toast");el.textContent=message;el.classList.add("visible");clearTimeout(toastTimeout);
    toastTimeout=setTimeout(()=>el.classList.remove("visible"),3200);}
  function apply(result,success){
    if(!result.ok){toast(result.error);return;}
    save();render();toast(success);
  }
  function go(view){
    const next=["inicio","casetas","licencias","socios","cartas","proyecto"].includes(view)?view:"inicio";
    active=next;
    document.querySelectorAll(".view").forEach(s=>s.hidden=s.id!=="view-"+next);
    document.querySelectorAll("[data-nav]").forEach(a=>a.classList.toggle("active",a.dataset.nav===next));
    $("current-section").textContent={
      inicio:"Inicio",casetas:"Casetas",licencias:"Licencias",socios:"Socios",cartas:"Cartas",proyecto:"El proyecto"
    }[next];
    $("sidebar").classList.remove("open");$("menu-toggle").setAttribute("aria-expanded","false");
    window.scrollTo({top:0,behavior:"instant"});render();
  }
  const iconArrow='<svg class="icon"><use href="#i-arrow"/></svg>';
  function renderCard(c){
    const n=c.socios.length;
    return '<button class="caseta-card" type="button" data-action="detail" data-id="'+c.id+'" '+
      'aria-label="Ver detalles de '+esc(c.nombre)+'">'+
      '<div class="caseta-preview scheme-'+c.id+'"><span class="preview-medallion" aria-hidden="true">✳</span></div>'+
      '<div class="caseta-card-bottom"><span class="card-kicker">'+(c.publica?"Caseta pública":"Caseta privada")+'</span>'+
      '<h3>'+esc(c.nombre)+'</h3><p>'+esc(c.razon)+'</p>'+
      '<div class="card-footer"><span>'+n+' / '+c.aforo+' socios</span><span>Ver ficha →</span></div>'+
      '</div></button>';
  }
  function renderOverview(){
    const m=counts(db);
    $("metrics").innerHTML=[
      ["Casetas registradas",m.casetas,"En el recinto","✳"],
      ["Socios inscritos",m.socios,"Miembros asociados","◈"],
      ["Licencias aprobadas",m.aprobadas,"Cupo municipal","✦"],
      ["Solicitudes pendientes",m.pendientes,"Por resolver","◉"]
    ].map(x=>'<article class="metric"><div class="metric-head"><span class="metric-title">'+x[0]+
      '</span><span class="metric-icon" aria-hidden="true">'+x[3]+'</span></div><strong>'+x[1]+
      '</strong><div class="metric-foot">'+x[2]+'</div></article>').join("");
    $("home-casetas").innerHTML=db.casetas.slice(0,4).map(renderCard).join("");
    $("home-requests").innerHTML=[...db.solicitudes].reverse().slice(0,4).map(s=>
      '<div class="activity-line"><div><strong>'+esc(caseta(s.casetaId)?.nombre||"Caseta")+
      '</strong><small>'+esc(ayto(s.aytoId)?.nombre||"Ayuntamiento")+'</small></div>'+badge(s.estado)+'</div>'
    ).join("")||'<div class="empty">Todavía no hay solicitudes.</div>';
  }
  function renderCasetas(){
    const subset=db.casetas.filter(c=>c.nombre.toLowerCase().includes(search.toLowerCase())&&
      (type==="all"||(type==="publica"?c.publica:!c.publica)));
    $("caseta-counter").textContent=subset.length+" "+(subset.length===1?"caseta":"casetas");
    $("caseta-grid").innerHTML=subset.length?subset.map(renderCard).join(""):'<div class="empty">No hay casetas que coincidan con los filtros.</div>';
  }
  function renderLicencias(){
    $("open-request").hidden=role!=="CASETA";
    $("quota-grid").innerHTML=db.ayuntamientos.map(a=>{
      const aprobadas=db.solicitudes.filter(s=>s.aytoId===a.id&&s.estado==="APROBADA").length;
      const pct=Math.min(100,a.limite?aprobadas/a.limite*100:100);
      return '<article class="quota-card"><div class="quota-title"><strong>'+esc(a.nombre)+'</strong><span>'+
        (a.limite-aprobadas)+' libres</span></div><div class="quota-numbers"><strong>'+aprobadas+' / '+a.limite+
        '</strong><small>Licencias aprobadas</small></div><div class="progress" aria-label="'+pct+
        '% del cupo ocupado"><div style="width:'+pct+'%"></div></div></article>';
    }).join("");
    let visible=db.solicitudes.filter(s=>role==="CASETA"?s.casetaId===1:
      role==="AYUNTAMIENTO"?s.aytoId===1:false);
    visible=visible.filter(s=>licenseFilter==="all"||s.estado===licenseFilter);
    $("license-rows").innerHTML=visible.length?visible.map(s=>{
      const actions=s.estado==="PENDIENTE"&&role==="AYUNTAMIENTO"?
        '<div class="table-actions"><button class="mini-action approve" data-action="approve" data-id="'+s.id+'">Aprobar</button>'+
        '<button class="mini-action reject" data-action="reject" data-id="'+s.id+'">Rechazar</button></div>':
        s.estado==="PENDIENTE"&&role==="CASETA"?
        '<button class="mini-action reject" data-action="withdraw" data-id="'+s.id+'">Retirar</button>':'—';
      return '<tr><td><strong>'+esc(caseta(s.casetaId)?.nombre||"Caseta")+'</strong><small>#'+s.id+
        '</small></td><td>'+esc(ayto(s.aytoId)?.nombre||"Ayuntamiento")+'</td><td>'+badge(s.estado)+
        '</td><td>'+actions+'</td></tr>';
    }).join(""):'<tr><td colspan="4" class="empty">'+(role==="CASETA"||role==="AYUNTAMIENTO"?
      'No hay solicitudes para esta vista y filtro.':'Selecciona Caseta o Ayuntamiento en la parte superior para consultar sus solicitudes.')+
      '</td></tr>';
    $("license-hint").textContent=role==="CASETA"?"Vista simulada de La Flor de Abril. Puedes presentar una solicitud y retirar las pendientes.":
      role==="AYUNTAMIENTO"?"Vista simulada del Ayuntamiento del Real. Solo permite resolver solicitudes pendientes dentro de su cupo.":
      "Las licencias privadas solo se muestran al seleccionar un perfil autorizado. Este selector no autentica contra la API.";
  }
  function renderSelectors(){
    const options=db.casetas.map(c=>'<option value="'+c.id+'">'+esc(c.nombre)+'</option>').join("");
    $("member-caseta").innerHTML=options;$("member-caseta").value=String(memberCaseta);
    $("menu-caseta").innerHTML=options;$("menu-caseta").value=String(menuCaseta);
  }
  function renderSocios(){
    const c=caseta(memberCaseta);if(!c)return;
    const authorized=role==="CASETA"&&c.id===1;
    $("member-occupancy").textContent=c.socios.length+" de "+c.aforo+" plazas ocupadas";
    const available=db.socios.filter(s=>!c.socios.includes(s.id));
    $("member-content").innerHTML='<div class="member-header"><h2>Socios de '+esc(c.nombre)+'</h2>'+
      (authorized?'<button class="action-button" type="button" data-action="open-member">+ Añadir socio</button>':'')+
      '</div><div class="member-grid">'+
      (c.socios.length?c.socios.map(id=>{
        const s=socio(id);if(!s)return"";
        return '<article class="member-card"><span class="person-avatar" aria-hidden="true">'+esc(s.nombre.slice(0,1))+
          '</span><div><strong>'+esc(s.nombre+" "+s.apellido)+'</strong><small>Socio registrado</small></div>'+
          (authorized?'<button class="mini-action" type="button" data-action="remove-member" data-id="'+id+
            '" title="Eliminar socio" aria-label="Eliminar a '+esc(s.nombre)+'">×</button>':'')+'</article>';
      }).join(""):'<div class="empty">Esta caseta aún no tiene socios.</div>')+
      '</div><p class="role-hint">'+(authorized?"Puedes gestionar socios de La Flor de Abril. El aforo limita las altas.":
      "Para gestionar socios, selecciona el perfil Caseta y la caseta La Flor de Abril.")+'</p>';
  }
  function renderCartas(){
    const c=caseta(menuCaseta);if(!c)return;
    const authorized=role==="CASETA"&&c.id===1;
    $("add-product").hidden=!authorized;
    $("product-content").innerHTML=c.productos.length?c.productos.map(p=>
      '<article class="product-card"><div class="product-top"><span class="product-category">'+esc(p.tipo)+
      '</span><span class="product-art" aria-hidden="true">'+(p.tipo==="BEBIDA"?"◉":"✳")+'</span></div>'+
      '<h3>'+esc(p.nombre)+'</h3><div class="product-bottom"><strong>'+euro(p.precio)+'</strong>'+
      (authorized?'<button class="mini-action reject" data-action="remove-product" data-id="'+p.id+'">Eliminar</button>':"")+
      '</div></article>').join(""):'<div class="empty">Esta caseta todavía no tiene productos.</div>';
  }
  function render(){renderOverview();renderCasetas();renderLicencias();renderSelectors();renderSocios();renderCartas();}
  const dialog=$("detail-dialog"),body=$("dialog-content");
  function open(html){body.innerHTML=html;dialog.showModal();}
  function close(){dialog.close();}
  function detail(id){
    const c=caseta(id);if(!c)return;
    open('<div class="dialog-head"><span class="eyebrow">CASETA / '+(c.publica?"PÚBLICA":"PRIVADA")+
      '</span><h2 id="dialog-title">'+esc(c.nombre)+'</h2><p>'+esc(c.razon)+'</p></div>'+
      '<div class="dialog-body"><div class="dialog-stats"><div><strong>'+c.aforo+'</strong><small>Aforo máximo</small></div>'+
      '<div><strong>'+c.socios.length+'</strong><small>Socios</small></div><div><strong>'+c.productos.length+
      '</strong><small>Productos</small></div></div><div class="dialog-section"><h3>Carta de la caseta</h3><div class="dialog-list">'+
      (c.productos.map(p=>'<div class="dialog-row"><span>'+esc(p.nombre)+' · '+esc(p.tipo)+'</span><span>'+
      euro(p.precio)+'</span></div>').join("")||'<p>No hay productos registrados.</p>')+'</div></div>'+
      '<div class="dialog-section"><h3>Socios registrados</h3><div class="dialog-list">'+
      (c.socios.map(id=>{const s=socio(id);return s?'<div class="dialog-row"><span>'+esc(s.nombre+" "+s.apellido)+
        '</span><span>Socio</span></div>':""}).join("")||'<p>No hay socios registrados.</p>')+
      '</div></div></div>');
  }
  function requestModal(){
    open('<div class="dialog-head"><span class="eyebrow">TRAMITACIÓN MUNICIPAL</span>'+
      '<h2 id="dialog-title">Nueva solicitud</h2><p>Como caseta La Flor de Abril, elige el ayuntamiento responsable.</p></div>'+
      '<div class="dialog-body"><form id="request-form"><div class="form-field"><label for="request-ayto">Ayuntamiento</label>'+
      '<select id="request-ayto" name="ayto" required>'+db.ayuntamientos.map(a=>
        '<option value="'+a.id+'">'+esc(a.nombre)+'</option>').join("")+'</select></div>'+
      '<p class="form-hint">No se permiten dos solicitudes pendientes o aprobadas para el mismo ayuntamiento.</p>'+
      '<button class="action-button" type="submit">Presentar solicitud '+iconArrow+'</button></form></div>');
  }
  function memberModal(){
    const c=caseta(memberCaseta);
    const available=db.socios.filter(s=>!c.socios.includes(s.id));
    open('<div class="dialog-head"><span class="eyebrow">GESTIÓN DE AFORO</span><h2 id="dialog-title">Añadir socio</h2>'+
      '<p>Plazas ocupadas: '+c.socios.length+' de '+c.aforo+'.</p></div><div class="dialog-body">'+
      (available.length?'<form id="member-form"><div class="form-field"><label for="member-new">Seleccionar socio</label>'+
      '<select name="socio" id="member-new" required>'+available.map(s=>
        '<option value="'+s.id+'">'+esc(s.nombre+" "+s.apellido)+'</option>').join("")+
      '</select></div><button class="action-button" type="submit">Añadir socio</button></form>':
      '<p>No quedan socios de ejemplo disponibles para añadir.</p>')+'</div>');
  }
  function productModal(){
    open('<div class="dialog-head"><span class="eyebrow">CARTA DE CASETA</span><h2 id="dialog-title">Añadir producto</h2>'+
      '<p>Se añadirá a La Flor de Abril.</p></div><div class="dialog-body"><form id="product-form">'+
      '<div class="form-field"><label for="product-name">Nombre del producto</label><input id="product-name" name="nombre" required maxlength="70" placeholder="Ej. Montadito de lomo"></div>'+
      '<div class="form-field"><label for="product-type">Tipo</label><select id="product-type" name="tipo"><option value="COMIDA">Comida</option><option value="BEBIDA">Bebida</option></select></div>'+
      '<div class="form-field"><label for="product-price">Precio (EUR)</label><input id="product-price" name="precio" type="number" min="0" max="1000" step="0.01" value="4.50" required></div>'+
      '<button class="action-button" type="submit">Guardar producto</button></form></div>');
  }
  document.addEventListener("click",event=>{
    const to=event.target.closest("[data-goto]");
    if(to){location.hash=to.dataset.goto;return;}
    const button=event.target.closest("[data-action]");if(!button)return;
    const action=button.dataset.action,id=Number(button.dataset.id);
    if(action==="detail")detail(id);
    if(action==="open-request")requestModal();
    if(action==="open-member")memberModal();
    if(action==="open-product")productModal();
    if(action==="approve")apply(decidirLicencia(db,id,"APROBADA",role),"Licencia aprobada (simulación).");
    if(action==="reject")apply(decidirLicencia(db,id,"RECHAZADA",role),"Solicitud rechazada (simulación).");
    if(action==="withdraw")apply(decidirLicencia(db,id,"ELIMINAR",role),"Solicitud retirada (simulación).");
    if(action==="remove-member")apply(quitarSocio(db,memberCaseta,id,role),"Socio eliminado de la caseta.");
    if(action==="remove-product")apply(quitarProducto(db,menuCaseta,id,role),"Producto eliminado de la carta.");
    if(action==="reset"){db=seed();save();render();toast("Se han restaurado los datos de ejemplo.");}
  });
  document.addEventListener("submit",event=>{
    const form=event.target;
    if(!["request-form","member-form","product-form"].includes(form.id))return;
    event.preventDefault();
    const fd=new FormData(form);
    let res,ok;
    if(form.id==="request-form"){res=crearSolicitud(db,1,Number(fd.get("ayto")),role);ok="Solicitud presentada (simulación).";}
    if(form.id==="member-form"){res=anadirSocio(db,memberCaseta,Number(fd.get("socio")),role);ok="Socio añadido correctamente.";}
    if(form.id==="product-form"){res=anadirProducto(db,menuCaseta,role,fd.get("nombre"),fd.get("tipo"),fd.get("precio"));ok="Producto añadido a la carta.";}
    if(res.ok)close();
    apply(res,ok);
  });
  $("active-role").addEventListener("change",e=>{
    role=e.target.value;render();toast("Vista de "+e.target.selectedOptions[0].text.toLowerCase()+" activada.");
  });
  $("caseta-search").addEventListener("input",e=>{search=e.target.value;renderCasetas();});
  $("caseta-type").addEventListener("change",e=>{type=e.target.value;renderCasetas();});
  $("license-filter").addEventListener("change",e=>{licenseFilter=e.target.value;renderLicencias();});
  $("member-caseta").addEventListener("change",e=>{memberCaseta=Number(e.target.value);renderSocios();});
  $("menu-caseta").addEventListener("change",e=>{menuCaseta=Number(e.target.value);renderCartas();});
  $("close-dialog").addEventListener("click",close);
  dialog.addEventListener("click",e=>{if(e.target===dialog)close();});
  $("menu-toggle").addEventListener("click",()=>{
    const isOpen=$("sidebar").classList.toggle("open");
    $("menu-toggle").setAttribute("aria-expanded",String(isOpen));
  });
  window.addEventListener("hashchange",()=>go(location.hash.slice(1)));
  go(location.hash.slice(1));
})();