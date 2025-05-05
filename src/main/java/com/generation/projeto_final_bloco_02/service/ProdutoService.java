package com.generation.projeto_final_bloco_02.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.generation.projeto_final_bloco_02.model.Produto;
import com.generation.projeto_final_bloco_02.model.ProdutoDto;
import com.generation.projeto_final_bloco_02.repository.ProdutoRepository;

@Service
public class ProdutoService {
    @Autowired
    private ProdutoRepository produtoRepository;

    public Optional<ProdutoDto> buscarProdutoComPromocao(Long id) {
        Optional<Produto> produtoOptional = produtoRepository.findById(id);
        
        return produtoOptional.map(produto -> {
            BigDecimal precoFinal = calcularPrecoFinal(produto);
            BigDecimal desconto = produto.getPreco().subtract(precoFinal);
    
            ProdutoDto produtoDto = new ProdutoDto();

            produtoDto.setId(produto.getId());
            produtoDto.setNome(produto.getNome());
            produtoDto.setDescricao(produto.getDescricao());
            produtoDto.setPreco(produto.getPreco());
            produtoDto.setEmPromocao(isEmPromocao(produto));
            produtoDto.setPrecoFinal(precoFinal);
            produtoDto.setDesconto(desconto);
            produtoDto.setCategoria(
                produto.getCategoria() != null ? produto.getCategoria().getNome() : null
            );
    
            return produtoDto;
        });
    }

    public List<ProdutoDto> listarProdutosEmPromocao() {
        return produtoRepository.findAllByEmPromocaoTrue().stream()
            .map(produto -> {
                BigDecimal precoFinal = calcularPrecoFinal(produto);
                BigDecimal desconto = produto.getPreco().subtract(precoFinal);

                ProdutoDto produtoDto = new ProdutoDto();

                produtoDto.setId(produto.getId());
                produtoDto.setNome(produto.getNome());
                produtoDto.setDescricao(produto.getDescricao());
                produtoDto.setPreco(produto.getPreco());
                produtoDto.setEmPromocao(true); 
                produtoDto.setPrecoFinal(precoFinal);
                produtoDto.setDesconto(desconto);
                produtoDto.setCategoria(produto.getCategoria() != null ? produto.getCategoria().getNome() : null);
            
            return produtoDto;
        })
        .toList();
    }

    private boolean isEmPromocao(Produto produto) {
        return produto.getEmPromocao() != null && produto.getEmPromocao();
    }

    private BigDecimal calcularPrecoFinal(Produto produto) {
        if (isEmPromocao(produto)) {
            return produto.getPreco().multiply(BigDecimal.valueOf(0.90)).setScale(2, RoundingMode.HALF_UP);
        }
        return produto.getPreco();
    }
}
