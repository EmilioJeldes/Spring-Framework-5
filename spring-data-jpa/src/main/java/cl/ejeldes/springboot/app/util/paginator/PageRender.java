package cl.ejeldes.springboot.app.util.paginator;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PageRender<T> {

    private String url;
    private Page<T> page;
    private List<PageItem> paginas;

    private int totalPaginas;
    private int numElementosPorPagina;
    private int paginaActual;
    private boolean first, last, hasNext, hasPrevious;

    public PageRender(String url, Page<T> page) {
        this.url = url;
        this.page = page;
        this.paginas = new ArrayList<>();

        numElementosPorPagina = page.getSize();
        totalPaginas = page.getTotalPages();
        paginaActual = page.getNumber() + 1;
        first = page.isFirst();
        last = page.isLast();
        hasNext = page.hasNext();
        hasPrevious = page.hasPrevious();

        int desde, hasta;

        if (totalPaginas <= numElementosPorPagina) {
            desde = 1;
            hasta = validarHasta(totalPaginas);
        } else {
            if (paginaActual <= numElementosPorPagina / 2) {
                desde = 1;
                hasta = validarHasta(numElementosPorPagina);
            } else if (paginaActual >= totalPaginas - numElementosPorPagina / 2) {
                desde = totalPaginas - numElementosPorPagina + 1;
                hasta = validarHasta(numElementosPorPagina);
            } else {
                desde = paginaActual - numElementosPorPagina / 2;
                hasta = validarHasta(numElementosPorPagina);
            }
        }

        for (int i = 0; i < hasta; i++) {
            paginas.add(new PageItem(desde + i, paginaActual == desde + i));
        }
    }

    private int validarHasta(int hasta) {
        return hasta > 10 ? 10 : hasta;
    }


}

