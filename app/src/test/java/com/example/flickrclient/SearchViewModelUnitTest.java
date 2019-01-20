package com.example.flickrclient;

import com.example.flickrclient.model.Photo;
import com.example.flickrclient.service.factory.Factory;
import com.example.flickrclient.service.factory.FactoryLocator;
import com.example.flickrclient.service.search.SearchApiRequest;
import com.example.flickrclient.service.search.SearchApiResponse;
import com.example.flickrclient.service.search.SearchExecutor;
import com.example.flickrclient.ui.SearchViewModel;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import android.arch.core.executor.testing.InstantTaskExecutorRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchViewModelUnitTest {

    private static Factory sFactoryMock;

    @Rule
    public InstantTaskExecutorRule taskExecutorRule = new InstantTaskExecutorRule();

    @BeforeClass
    public static void setup()
    {
        sFactoryMock = mock(Factory.class);
        FactoryLocator.Register(sFactoryMock);
    }

    @Test
    public void search_executorIsInvoked() {
        SearchViewModel viewModel = new SearchViewModel();
        final String query = "kittens";
        SearchExecutor searchMock = mock(SearchExecutor.class);
        when(sFactoryMock.createSearchTask(viewModel)).thenReturn(searchMock);

        viewModel.search(query);

        ArgumentCaptor<SearchApiRequest> argument = ArgumentCaptor.forClass(SearchApiRequest.class);
        verify(searchMock).executeRequest(argument.capture());
        assertEquals(query, argument.getValue().getQueryString());
        assertEquals(1, argument.getValue().getPage());
    }

    @Test
    public void search_statusIsRunning() {
        SearchViewModel viewModel = new SearchViewModel();
        final String query = "kittens";
        SearchExecutor searchMock = mock(SearchExecutor.class);
        when(sFactoryMock.createSearchTask(viewModel)).thenReturn(searchMock);

        assertEquals(SearchViewModel.SearchStatus.IDLE, viewModel.getSearchStatus().getValue());

        viewModel.search(query);

        assertEquals(SearchViewModel.SearchStatus.RUNNING, viewModel.getSearchStatus().getValue());
    }

    @Test
    public void searchCompletion_onSuccess() {
        List<Photo> photoList = new ArrayList<>();
        photoList.add(new Photo("",""));
        SearchApiResponse response = new SearchApiResponse(SearchApiResponse.Status.SUCCESS, photoList, 1, 1);
        SearchViewModel viewModel = new SearchViewModel();
        viewModel.onSearchCompleted(response);
        assertEquals(photoList, viewModel.getPhotos().getValue());
        assertEquals(SearchViewModel.SearchStatus.IDLE, viewModel.getSearchStatus().getValue());
        assertEquals(1, viewModel.getCurrentPage());
    }

    @Test
    public void searchCompletion_onSuccess_nextPage() {
        List<Photo> photoList1 = new ArrayList<>();
        photoList1.add(new Photo("",""));
        SearchApiResponse response1 = new SearchApiResponse(SearchApiResponse.Status.SUCCESS, photoList1, 1, 2);
        SearchViewModel viewModel = new SearchViewModel();
        viewModel.onSearchCompleted(response1);

        List<Photo> photoList2 = new ArrayList<>();
        photoList2.add(new Photo("",""));
        SearchApiResponse response2 = new SearchApiResponse(SearchApiResponse.Status.SUCCESS, photoList2, 2, 1);
        viewModel.onSearchCompleted(response2);

        List<Photo> expectedList = new ArrayList<>();
        expectedList.addAll(photoList1);
        expectedList.addAll(photoList2);
        assertEquals(expectedList, viewModel.getPhotos().getValue());
        assertEquals(SearchViewModel.SearchStatus.IDLE, viewModel.getSearchStatus().getValue());
    }

    @Test
    public void searchCompletion_onError() {
        SearchApiResponse response = new SearchApiResponse(SearchApiResponse.Status.ERROR, Collections.<Photo>emptyList(), 1, 1);
        SearchViewModel viewModel = new SearchViewModel();
        viewModel.onSearchCompleted(response);
        assertEquals(SearchViewModel.SearchStatus.ERROR, viewModel.getSearchStatus().getValue());
    }

    @Test
    public void searchCompletion_onCancelled() {
        SearchApiResponse response = new SearchApiResponse(SearchApiResponse.Status.CANCELLED, Collections.<Photo>emptyList(), 1, 1);
        SearchViewModel viewModel = new SearchViewModel();
        viewModel.onSearchCompleted(response);
        assertEquals(SearchViewModel.SearchStatus.IDLE, viewModel.getSearchStatus().getValue());
    }

    @Test
    public void fetchNextPage_whenIdle() {
        SearchViewModel viewModel = new SearchViewModel();
        final String query = "kittens";
        SearchExecutor searchMock = mock(SearchExecutor.class);
        when(sFactoryMock.createSearchTask(viewModel)).thenReturn(searchMock);

        viewModel.search(query);

        List<Photo> photoList = new ArrayList<>();
        photoList.add(new Photo("",""));
        SearchApiResponse response = new SearchApiResponse(SearchApiResponse.Status.SUCCESS, photoList, 1, 10);
        viewModel.onSearchCompleted(response);
        assertEquals(SearchViewModel.SearchStatus.IDLE, viewModel.getSearchStatus().getValue());
        assertEquals(1, viewModel.getCurrentPage());

        SearchExecutor searchMock2 = mock(SearchExecutor.class);
        when(sFactoryMock.createSearchTask(viewModel)).thenReturn(searchMock2);

        viewModel.fetchNextPage();

        ArgumentCaptor<SearchApiRequest> argument = ArgumentCaptor.forClass(SearchApiRequest.class);
        verify(searchMock2).executeRequest(argument.capture());
        assertEquals(query, argument.getValue().getQueryString());
        assertEquals(2, argument.getValue().getPage());
        assertEquals(SearchViewModel.SearchStatus.RUNNING, viewModel.getSearchStatus().getValue());
    }

    @Test
    public void fetchNextPage_whenRunning() {
        SearchViewModel viewModel = new SearchViewModel();
        final String query = "kittens";
        SearchExecutor searchMock = mock(SearchExecutor.class);
        when(sFactoryMock.createSearchTask(viewModel)).thenReturn(searchMock);

        viewModel.search(query);

        List<Photo> photoList = new ArrayList<>();
        photoList.add(new Photo("",""));
        SearchApiResponse response = new SearchApiResponse(SearchApiResponse.Status.SUCCESS, photoList, 1, 10);

        viewModel.onSearchCompleted(response);

        assertEquals(SearchViewModel.SearchStatus.IDLE, viewModel.getSearchStatus().getValue());
        assertEquals(1, viewModel.getCurrentPage());

        SearchExecutor searchMock2 = mock(SearchExecutor.class);
        when(sFactoryMock.createSearchTask(viewModel)).thenReturn(searchMock2);

        viewModel.fetchNextPage();

        assertEquals(SearchViewModel.SearchStatus.RUNNING, viewModel.getSearchStatus().getValue());

        viewModel.fetchNextPage();

        verify(sFactoryMock, times(2)).createSearchTask(viewModel);
        assertEquals(SearchViewModel.SearchStatus.RUNNING, viewModel.getSearchStatus().getValue());
    }

    @Test
    public void fetchNextPage_whenNoMorePages() {
        SearchViewModel viewModel = new SearchViewModel();
        final String query = "kittens";
        SearchExecutor searchMock = mock(SearchExecutor.class);
        when(sFactoryMock.createSearchTask(viewModel)).thenReturn(searchMock);

        viewModel.search(query);

        List<Photo> photoList = new ArrayList<>();
        photoList.add(new Photo("",""));
        final int numPagesRemaining = 0;
        SearchApiResponse response = new SearchApiResponse(SearchApiResponse.Status.SUCCESS, photoList, 1, numPagesRemaining);

        viewModel.onSearchCompleted(response);

        assertEquals(SearchViewModel.SearchStatus.IDLE, viewModel.getSearchStatus().getValue());
        assertEquals(1, viewModel.getCurrentPage());

        viewModel.fetchNextPage();

        assertEquals(SearchViewModel.SearchStatus.IDLE, viewModel.getSearchStatus().getValue());
        verify(sFactoryMock, times(1)).createSearchTask(viewModel);
    }
}
