package com.emsi.jaxrs.rest;

import com.emsi.jaxrs.entities.Compte;
import com.emsi.jaxrs.repositories.CompteRepository;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Path("/banque")
public class CompteRestJaxRSAPI {

    @Autowired
    private CompteRepository compteRepository;

    @Path("/comptes")
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON}) 
    public List<Compte> getComptes() {
        return compteRepository.findAll();
    }

    @Path("/comptes/{id}")
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON}) 
    public Compte getCompte(@PathParam("id") Long id) {
        return compteRepository.findById(id).orElse(null);
    }

    @Path("/comptes")
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON}) 
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Compte addCompte(Compte compte) {
        return compteRepository.save(compte);
    }

    @Path("/comptes/{id}")
    @PUT
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Compte updateCompte(@PathParam("id") Long id, Compte compte) {
        Compte existingCompte = compteRepository.findById(id).orElse(null);
        if (existingCompte != null) {
            existingCompte.setSolde(compte.getSolde());
            existingCompte.setDateCreation(compte.getDateCreation());
            existingCompte.setType(compte.getType());
            return compteRepository.save(existingCompte);
        }
        return null;
    }

    @Path("/comptes/{id}")
    @DELETE
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON}) // Optional: modify if you want to return something
    public void deleteCompte(@PathParam("id") Long id) {
        compteRepository.deleteById(id);
    }
}
