package mrRebujito.MrRebujito.service;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import mrRebujito.MrRebujito.entity.Caseta;
import mrRebujito.MrRebujito.entity.Producto;
import mrRebujito.MrRebujito.repository.CasetaRepository;
import mrRebujito.MrRebujito.repository.ProductoRepository;
import mrRebujito.MrRebujito.security.JWTUtils;

@Service
public class ProductoService {
    private final ProductoRepository productos;
    private final CasetaRepository casetas;
    private final JWTUtils jwt;
    public ProductoService(ProductoRepository productos,CasetaRepository casetas,JWTUtils jwt){
        this.productos=productos;this.casetas=casetas;this.jwt=jwt;
    }
    public List<Producto> findAllProducto(){return productos.findAll();}
    public Optional<Producto> findProductoById(int id){return productos.findById(id);}
    private Caseta casetaPropietaria(){
        Caseta session=jwt.userLogin();
        if(session==null)throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return casetas.findByIdForUpdate(session.getId()).orElseThrow();
    }
    @Transactional
    public Producto saveProducto(Producto data){
        Caseta c=casetaPropietaria();
        Producto p=new Producto();
        p.setNombre(data.getNombre());p.setTipoAlimento(data.getTipoAlimento());p.setPrecio(data.getPrecio());
        p=productos.save(p);
        c.getProductos().add(p);casetas.save(c);
        return p;
    }
    @Transactional
    public Producto updateProducto(int id,Producto data){
        Caseta c=casetaPropietaria();
        if(c.getProductos().stream().noneMatch(p->p.getId()==id))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"El producto no pertenece a tu caseta");
        Producto p=productos.findById(id).orElseThrow(
            ()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Producto no encontrado"));
        p.setNombre(data.getNombre());p.setTipoAlimento(data.getTipoAlimento());p.setPrecio(data.getPrecio());
        return productos.save(p);
    }
    @Transactional
    public void deleteById(int id){
        Caseta c=casetaPropietaria();
        if(!c.getProductos().removeIf(p->p.getId()==id))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"El producto no pertenece a tu caseta");
        casetas.saveAndFlush(c);
        // El producto solo se borra si no está referenciado en otra carta.
        if(!productos.existsById(id))return;
        if(casetas.findAll().stream().noneMatch(other->
            other.getProductos().stream().anyMatch(p->p.getId()==id)))productos.deleteById(id);
    }
}
